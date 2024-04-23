import java.util.*;
//necessary libraries for scheduled execution choking peers
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static java.util.stream.Collectors.toMap;

public class Choke implements Runnable {
    public int chokingInterval;
    public int maxNeighbors;
    public Peer hostPeer;
    public ScheduledExecutorService scheduler = null;

    // choke constructor to initialize the choking interval and max neighbors
    public Choke(Peer hostPeer) {
        this.hostPeer = hostPeer;
        this.chokingInterval = hostPeer.peerConfig.getUnchokingInterval();
        this.maxNeighbors = hostPeer.peerConfig.getNumberOfPreferredNeighbors();

    }

    public void chokePeriodically() {
        // schedule the choking of peers
        // modify initial delay if necessary
        scheduler.scheduleAtFixedRate(this, 6, chokingInterval, TimeUnit.SECONDS);
    }

    // run method will periodically update the which peers are choked and unchoked
    public void run() {
        try {
            // get the list of unchoked peers and interested peers
            HashSet<String> unchokedPeers = hostPeer.unchokedList;
            List<String> interestedPeers = new ArrayList<String>(hostPeer.interList);

            // create random object to randomly select peers
            Random rand = new Random();

            HashSet<String> unchokeCandidates = new HashSet<String>();

            //consider interested Peers
            if (interestedPeers.size() > 0) // if there are any peers interested
            {
                // case where peer has complete file
                if (hostPeer.checkAllPiecesDownloaded()) {
                    int candidates = interestedPeers.size();
                    if (maxNeighbors < candidates) {
                        candidates = maxNeighbors;
                    }

                    for (int i = 0; i < candidates; i++) {
                        //randomly select a peer
                        int randIndex = rand.nextInt(interestedPeers.size());
                        String currPeer = interestedPeers.get(randIndex);
                        PeerConnection currPeerConnection = hostPeer.connectedPeers.get(currPeer);

                        // ensure a peer is not selected more than once
                        while (unchokeCandidates.contains(currPeer) == false) {
                            randIndex = rand.nextInt(interestedPeers.size());
                            currPeer = interestedPeers.get(randIndex);
                            //get selected peer connection
                            currPeerConnection = hostPeer.connectedPeers.get(currPeer);
                        }

                        //unchoke the peer if they are not alreadu unchoked (including optimistically unchoked)
                        if (!unchokedPeers.contains(currPeer) &&
                                (hostPeer.currOptUnchokedId == null || !hostPeer.currOptUnchokedId.equals(currPeer))) {
                            currPeerConnection.UnchokedMsg();
                        } else {
                            // remove the peer from the unchoked list
                            unchokedPeers.remove(currPeer);
                        }
                        unchokeCandidates.add(currPeer);
                        currPeerConnection.downloadrate = 0;
                    }
                } else {
                    //unchoke based on download rates
                    unchokeBasedOnRates(interestedPeers.size(), interestedPeers, unchokedPeers, unchokeCandidates);
                }
            } else {
                // clean up
                clearUnchoke(unchokeCandidates);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void clearUnchoke(HashSet<String> unchokedList)
    {
        hostPeer.unchokedList.clear();
        //send a choke message to every peer in unchoked list
        for (String p : unchokedList) {
            PeerConnection currConnection = hostPeer.connectedPeers.get(p);
            currConnection.ChokeMsg();
        }

        // uf all peers have finished, stop everything
        if(hostPeer.checkCompleted()) {
            hostPeer.stopPeer();
        }
    }

    public void unchokeBasedOnRates(int candidates, List<String> interestedPeers, HashSet<String> unchokedPeers, HashSet<String> unchokedCandidates) {
        Map<String, Integer> downloadRates = new HashMap<>(hostPeer.getPeerRates());
        Map<String, Integer> sortedRates = downloadRates.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

        // now we have sorted rates in descending order

        Iterator<Map.Entry<String, Integer>> rateIterator = sortedRates.entrySet().iterator();
        int count = 0;
        while (count < candidates && rateIterator.hasNext()) {
            Map.Entry<String,Integer> nextRate = rateIterator.next();
            String peerId = nextRate.getKey();
            //must be in the interested map
            if(interestedPeers.contains(peerId)){
                //obtain their peer connection information
                PeerConnection potentialUnchoke = hostPeer.connectedPeers.get(peerId);
                //if it is already in the unchoked list, we have to take it out since it will be redundant
                if(unchokedPeers.contains(peerId)) {
                    unchokedPeers.remove(peerId);
                }
                else {
                    String optimisticallyUnchoked = hostPeer.currOptUnchokedId;
                    if(optimisticallyUnchoked == null || !optimisticallyUnchoked.equals(peerId)) {
                        potentialUnchoke.UnchokedMsg(); // this now sends the unchoked message
                    }
                }
                unchokedCandidates.add(peerId); // add the peer to the unchoked candidates
                potentialUnchoke.downloadrate = 0; // reset the download rate
                count++;
            }
        }

    }

    // stop the scheduler from periodically choking peers
    // will be called upon completion
    public void shutdown(){
        scheduler.shutdownNow();
    }
}