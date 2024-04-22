// Each Peer will have one instance of this Peer class

// This class is responsible for maintaining the state of each Peer
/*
    member variables to track
    - connected peers
    - optimistically unchoked peer
    - bitfield for each peer

    Furthermore:
    each Peer will have multiple PeerConnections (one for each peer in the network)
    Loaded in Config ( to get peerId, timing intervals and more)
    one Server instance

 */


import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.File;

public class Peer {

    public CommonCfgObject peerConfig;
    //contains:
    // - numberOfPreferredNeighbors
    // - unchokingInterval
    // - optimisticUnchokingInterval
    // - fileName
    // - fileSize
    // - pieceSize

    public PeerInfo peerInfo;
    //contains:
    // - peerId
    // - peerAddress
    // - peerPort
    // - hasFile

    int numPieces; // number of pieces in the file
    String requestTracker[]; // tracks which pieces have been requested already.
    // example usage:
    // if this Peer requests piece 10 to Peer 1006, then requestTracker[10] = "1006"
    // this denotes the request piece 10 was made to Peer 1006.
    // if an index is null, then that piece has not been requested yet.

    public TreeSet<String> peersInNetwork; // set of all peers (peerId's) in the network
    public HashMap<String, PeerConnection> connectedPeers; // map of connected peers
    // key: peerId, value: PeerConnection object.
    // Each Peer will have n-1 PeerConnections (where n is the number of peers in the network)

    public SortedMap<String, PeerInfo> allPeerInfoMap; // map of all peerr's PeerInfo
    // key: peerId, value: PeerInfo object.

    public RandomAccessFile fileBuilder; // file object to read and write the file
    // RandomAccessFile allows writes and reads to be made at any position in the file

    //networking variables
    public PeerServer peerServer;

    boolean finished;

    public HashMap<String, Thread> threadMap;

    //logger
    public P2PLog logger;

    public HashMap<String, BitSet> bitfieldMap;
    // key: peerId, value: BitSet object.
    // bitset object will be used to track which pieces of the file each peer has.
    public String currOptUnchokedId = null;
    // peerId corresponding to each peer that is optimistically unchoked
    //public OptUnchoked currOptUnchoked;
    //public Choke currChoke;

    public ArrayList<String> peerList;

    public HashSet<String> unchokedList;

    public HashSet<String> interList;

    public Thread thread;



    // constructor to initialize the peerObject.
    public Peer (String pId) throws IOException {
        this.finished = false;
        //read in config files and peer info files
        this.peerConfig = BufferReaderCommonCfg.reader();
        this.allPeerInfoMap = BufferReaderPeerInfo.reader();
        this.peerInfo = allPeerInfoMap.get(pId);
        this.peersInNetwork = new TreeSet<>(BufferReaderPeerInfo.allPeers);

        //start logger for peer
        this.logger = new P2PLog(pId);

        // set up additional structures that will be maintained by the peer
        this.connectedPeers = new HashMap<>();
        this.numPieces = (int) Math.ceil((double) this.peerConfig.getFileSize() / this.peerConfig.getPieceSize());
        this.requestTracker = new String[this.numPieces]; // each index corresponds to a piece.
        this.fileBuilder = null;
        this.peerServer = null;
        this.threadMap = new HashMap<>();
        this.bitfieldMap = new HashMap<>();
        this.unchokedList = new HashSet<>();
        this.interList = new HashSet<>();

        setUpPeer();

        // start choke/unchoke cycle:
        //startChokeUnchokeCycle();
    }

    public void setUpPeer() throws IOException {
        //set up file and directory
        String filePath = "peer_" + this.peerInfo.peerId;
        File file = new File(filePath);
        file.mkdir();
        file = new File(filePath + "/" + this.peerConfig.getFileName());
        //if peer does not have file than create it
        if (peerInfo.peerHasFile.equals("0")) {
            file.createNewFile();
        }

        //initialize the random access file
        // random access file will be used to read and write the file
        //this makes it easier to read and write to the file in a random specified position
        fileBuilder = new RandomAccessFile(file, "rw");
        // file should be the same length as the original file
        fileBuilder.setLength(peerConfig.getFileSize());

        //initialize the bitfield
        //make a helper method to fill our peer's bitfield
      String[] peerIds = peersInNetwork.toArray(new String[0]);
       for(int i = 0; i < peerIds.length; i++){
           // Creating BitSet for the bitfield to track the pieces of the file each peer holds
           BitSet bitfield = new BitSet(numPieces);
           String peerId = peerIds[i];
              // if the peer has the file, set all bits the corresponding index
                if (allPeerInfoMap.get(peerId).peerHasFile.equals("1")) {
                    //sets each index to the number of the piece index
                    // therefore all the numbers in the array are the available pieces
                    bitfield.set(0, numPieces);
                    this.bitfieldMap.put(peerId, bitfield); // the corresponding bitfield with the peerId
                }
                else{
                    //the bitfield array is empty because
                    bitfield.clear(); // set all bits to 0 since the peer does not have the file
                    this.bitfieldMap.put(peerId, bitfield); // the corresponding bitfield with the peerId
                }
       }

        //initialize the server
        peerServer = new PeerServer(peerInfo, this);

        // Start the server in a new thread
        this.thread = new Thread(() -> peerServer.run());
        this.thread.start();

        //connect to the neighbors
        System.out.println("Trying to connect to peers...");
            try {
                //Thread.sleep(5000); //temporary sleep to allow all peers to start
                for (String peerIdInNetwork : peersInNetwork) {
                    if (!peerIdInNetwork.equals(peerInfo.peerId)) {
                        //skip connection to self:
                        System.out.println("Trying to connect to Peer " + peerIdInNetwork);

                        // establish connection with each peer in the network
                        // Each Peer will have n-1 PeerConnections (where n is the number of peers in the network)
                        int otherPeerPort = Integer.parseInt(allPeerInfoMap.get(peerIdInNetwork).peerPort);
                        Socket interPeerSocket = new Socket(peerInfo.peerAddress, otherPeerPort);
                        PeerConnection peerConnection = new PeerConnection(this, interPeerSocket);
                        peerConnection.otherPeerID = this.peerInfo.peerId;
                        peerConnection.start = true;
                        // add the peerConnection to the connectedPeers set
                        connectedPeers.put(peerIdInNetwork, peerConnection);
                        // start the thread for the peerConnection
                        // a thread must be started for each peer connection to run in parallel
                        Thread peerConnectionThread = new Thread(peerConnection);
                        // need a map to make sure which thread refers to which peer connection
                        threadMap.put(peerIdInNetwork, peerConnectionThread);
                        threadMap.get(peerIdInNetwork).start();

                        System.out.println("Peer " + peerInfo.peerId + " is connected to Peer " + peerIdInNetwork);

                        // DON'T CONTINUE UNTIL WE FIGURE OUT THE CONNECTION BETWEEN PEERS LOCALLY
                    } else {
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


    }

    /*
    public void startChokeUnchokeCycle(){
        // start the choke/unchoke cycle
        this.currChoke = new Choke(this);
        this.currOptUnchoked = new OptUnchoked(this);

        //TODO:
        //missing code to terminate the threads and clean up

        currChoke.chokePeriodically();
        currOptUnchoked.UnchokedPeriodically();
    }

    public PeerConnection getPeerConnection(String peerId){
        return connectedPeers.get(peerId);
    }


    // additional peer methods:

    // read and write from/to using the random access file
    public synchronized byte[] readPiece(int pieceIndex)
    {
        // read the piece at the pieceIndex from the file
        byte[] pieceData = null;
        try {
            // account for last piece not being an entire piece
            if (pieceIndex == numPieces - 1)
            {
                // in this case we have to calculate the size of the last piece
                // since it does not match up to getPieceSize
                int lastPieceSize = peerConfig.getFileSize() % peerConfig.getPieceSize();
                pieceData = new byte[lastPieceSize];
                fileBuilder.seek(pieceIndex * peerConfig.getPieceSize());
                fileBuilder.read(pieceData);
            }
            else
            {
                pieceData = new byte[peerConfig.getPieceSize()];
                fileBuilder.seek(pieceIndex * peerConfig.getPieceSize());
                fileBuilder.read(pieceData);
            }
            return pieceData;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return pieceData;
    }

    public synchronized void writePiece(int pieceIndex, byte[] pieceData) throws IOException {
        // write the pieceData to the file at the pieceIndex
        try {
            fileBuilder.seek(pieceIndex * peerConfig.getPieceSize());
            fileBuilder.write(pieceData);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void HaveMessage()
    {
        // send the have message to all connected peers
        Set<String> currentPreferredNeighbors = connectedPeers.keySet();
        for (String peerId : currentPreferredNeighbors)
        {
            //TODO: implement sendHaveMessage in PeerConnection
            // connectedPeers.get(peerId).sendHaveMessage();
        }
    }

    //update bitfield after acquiring a new piece
    public synchronized void updateBitfield(String peerId, int pieceIndex)
    {
        bitfieldMap.get(peerId).set(pieceIndex);
    }

    //update bitfield after receiving a bitfield message
    public synchronized void updateEntireBitfield(String peerId, BitSet updated)
    {
        bitfieldMap.put(peerId, updated);
    }

    public synchronized boolean interestCheck(String otherPeer){
        // check if the host is interested in the other peer
        for (int i = 0 ; i < numPieces && i < bitfieldMap.get(otherPeer).length(); i++){
            if ((bitfieldMap.get(otherPeer)).get(i) && !(bitfieldMap.get(peerInfo.peerId)).get(i)){
                // if other peer has a piece in his bitfield that
                // the host does not have, then the host is interested
                return true;
            }
        }
        // compared entire bitfields and did not have any pieces needed.
        return false;
    }

    // function to get the download rates of all peers in preferred Neighbors
    // returns a map of peerId to download rate
    public synchronized HashMap<String, Integer> getPeerRates()
    {
        HashMap<String, Integer> peerRates = new HashMap<>();
        Set<String> currentPreferredNeighbors = connectedPeers.keySet();
        for (String peerId : currentPreferredNeighbors)
        {
            int peerRate = 0; // CHANGE THIS TO GET THE DOWNLOAD RATE AFTER THAT IS IMPLEMENTED
            // TODO: getDownloadRate in PeerConnection
            //int peerRate = connectedPeers.get(peerId).getDownloadRate();
            peerRates.put(peerId, peerRate);
        }
        return peerRates;
    }

    public  synchronized void updateConnectedPeers(String peerId, PeerConnection peerConnection){
        connectedPeers.put(peerId, peerConnection);
    }

    public synchronized void addThread(String peerId, Thread thread){
        threadMap.put(peerId, thread);
    }

    public synchronized void stopAllThreads()
    {
        Set<String> peerIds = threadMap.keySet();
        for (String peerId : peerIds)
        {
            threadMap.get(peerId).interrupt(); //don't know if interrupt or stop
        }
    }

    public synchronized void stopPeer(){
        //TODO
        //stop all threads
        //close all sockets
        //close all file streams
        // whatever else clean up needs to be done

    }


    // request tracking related methods:
    public synchronized void updateRequestTracker(int pieceIndex, String peerId)
    {
        // mark in the request tracker that piece [pieceIndex] was requested from peer <peerId>
        requestTracker[pieceIndex] = peerId;
    }

    public synchronized int checkRequested(String otherPeerID) {
        // for each piece in the bitfield of the other peer, check if the host has not requested it
        for (int i = 0; i < (bitfieldMap.get(otherPeerID)).size() && i < numPieces; i++)
        {
            if ( bitfieldMap.get(otherPeerID).get(i) && !(bitfieldMap.get(peerInfo.peerId).get(i)) && requestTracker[i] == null)
            {
                updateRequestTracker(i, otherPeerID);
                return i; // return the piece index
            }
        }
        return -1;
    }

    public synchronized void clearRequestTracker(String peerId)
    {
        for (int i = 0; i < requestTracker.length; i++){
            if (requestTracker[i] != null && requestTracker[i].equals(peerId)){
                requestTracker[i] = null;
            }
        }
    }

    public synchronized void addInterestedPeer(String peerId){
        interList.add(peerId);
    }
    public synchronized void removeInterestedPeer(String peerId){
        interList.remove(peerId);
    }

    //returns true if all peers have the complete file
    public synchronized boolean checkCompleted()
    {
        Set<String> peerIds = bitfieldMap.keySet();
        for (String peerId : peerIds)
        {
            if (!bitfieldMap.get(peerId).equals(numPieces))
            {
                return false;
            }
        }
        return true;
    }
    */

    public P2PLog getLog(){
        return this.logger;
    }

}


