//<<<<<<< Updated upstream
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
        this.interval = peer.peerConfig.getOptimisticUnchokingInterval();
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
        String prevOptPeer = p.currOptUnchokedId;
        List<String> intPeers = new ArrayList<>(p.peerList);
        intPeers.remove(prevOptPeer);

        String newOptUnchokedPeer = newOptUnchokedPeer(intPeers);

        if(newOptUnchokedPeer == null){
            checkOldOptPeer(prevOptPeer);
        }
        else{
            handleOptUnchoke(newOptUnchokedPeer, prevOptPeer);
        }


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

    private void handleOptUnchoke(String newOptUnchokePeer, String oldOptUnchokePeer){

        //set new unchoked peer
        p.currOptUnchokedId = newOptUnchokePeer;

        //send unchoked message for new peer and log it
        p.connectedPeers.get(newOptUnchokePeer).UnchokedMsg();
        p.getLog().logOptimisticallyUnchokedPeer(newOptUnchokePeer);

        //if the old optmistically unchoked peer exists and the unchoked List still has it, then we send a choked msg
        if(oldOptUnchokePeer != null && !p.unchokedList.contains(oldOptUnchokePeer)){
            p.connectedPeers.get(oldOptUnchokePeer).ChokeMsg();
        }
    }

    private void checkOldOptPeer(String oldOptUnchokePeer){
        //same if statement as the other method but this is meant as a backup check
        if(oldOptUnchokePeer != null && !p.unchokedList.contains(oldOptUnchokePeer)){
            p.connectedPeers.get(oldOptUnchokePeer).ChokeMsg();
        }

        //We want to check to see if we are done, before we shutdown the class
        if(p.finished){
            //p.stopAllThreads();
            //p.stopPeer();
        }
    }

    //stop scheduler
    public void stop(){
        this.scheduler.shutdownNow();
    }
}
//=======
//import java.util.*;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//public class OptUnchoked implements Runnable {
//
//    private int interval;
//    private Peer p;
//    private Random rand = new Random();
//    private ScheduledExecutorService scheduler = null;
//
//
//    public OptUnchoked(Peer peer){
//        this.p = peer;
//        //this.interval = peer.
//        this.scheduler = Executors.newScheduledThreadPool(1);
//    }
//
//    public void UnchokedPeriodically(){
//        scheduler.scheduleAtFixedRate(this, 6, this.interval, TimeUnit.SECONDS);
//    }
//
//
//    @Override
//    public void run() {
//        try{
//            performUnchoking();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    //main method to run: gets a new optimistically unchoked peer from list,
//    //and performs the handler logic to run smoothly
//    private void performUnchoking(){
//        String prevOptPeer = p.optunchokedPeer;
//        List<String> intPeers = new ArrayList<>(p.peerList);
//        intPeers.remove(prevOptPeer);
//
//        String newOptUnchokedPeer = newOptUnchokedPeer(intPeers);
//
//
//    }
//
//    //get a new peer to optimistically unchoke
//    private String newOptUnchokedPeer(List<String> peers){
//
//        while(!peers.isEmpty()){
//            String prospect = peers.get(rand.nextInt(peers.size()));
//
//            if(!p.unchokedList.contains(prospect)){
//                return prospect;
//            }
//
//            peers.remove(prospect);
//        }
//        return null;
//    }
//
//    private void handleOptUnchoke(String newOptUnchokePeer, String oldOptUnchokepeer){
//
//        //set new unchoked peer
//        p.optunchokedPeer = newOptUnchokePeer;
//
//        //add more to it -> we Need Peer to work
//
//    }
//
//    //stop scheduler
//    public void stop(){
//        this.scheduler.shutdownNow();
//    }
//}
//>>>>>>> Stashed changes