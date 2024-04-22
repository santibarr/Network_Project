import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OptUnchoked implements Runnable {

    private int interval;
    private Peer p;
    private Random rand = new Random();
    private ScheduledExecutorService scheduler = null;


    public OptUnchoked(Peer peer){
        this.p = peer;
        //this.interval = peer.
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void UnchokedPeriodically(){
        scheduler.scheduleAtFixedRate(this, 6, this.interval, TimeUnit.SECONDS);
    }


    @Override
    public void run() {
        try{
            performUnchoking();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //main method to run: gets a new optimistically unchoked peer from list,
    //and performs the handler logic to run smoothly
    private void performUnchoking(){
        String prevOptPeer = p.optunchokedPeer;
        List<String> intPeers = new ArrayList<>(p.peerList);
        intPeers.remove(prevOptPeer);

        String newOptUnchokedPeer = newOptUnchokedPeer(intPeers);


    }

    //get a new peer to optimistically unchoke
    private String newOptUnchokedPeer(List<String> peers){

        while(!peers.isEmpty()){
            String prospect = peers.get(rand.nextInt(peers.size()));

            if(!p.unchokedList.contains(prospect)){
                return prospect;
            }

            peers.remove(prospect);
        }
        return null;
    }

    private void handleOptUnchoke(String newOptUnchokePeer, String oldOptUnchokepeer){

        //set new unchoked peer
        p.optunchokedPeer = newOptUnchokePeer;

        //add more to it -> we Need Peer to work

    }

    //stop scheduler
    public void stop(){
        this.scheduler.shutdownNow();
    }
}