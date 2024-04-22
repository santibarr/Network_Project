//<<<<<<< Updated upstream
import java.util.*;
//necessary libraries for scheduled execution choking peers
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static java.util.stream.Collectors.toMap;

public class Choke implements Runnable{
    public int chokingInterval;
    public int maxNeighbors;
    public Peer hostPeer;
    public ScheduledExecutorService scheduler = null;

    // choke constructor to initialize the choking interval and max neighbors
    public Choke(Peer hostPeer)
    {
        this.hostPeer = hostPeer;
        this.chokingInterval = hostPeer.peerConfig.getUnchokingInterval();
        this.maxNeighbors = hostPeer.peerConfig.getNumberOfPreferredNeighbors();
    }

    public void chokePeriodically(){
        // schedule the choking of peers
        // modify initial delay if necessary
        scheduler.scheduleAtFixedRate(this, 6, chokingInterval, TimeUnit.SECONDS);
    }

    // run method will periodically update the which peers are choked and unchoked
    public void run() {
        try {
            // get the list of unchoked peers and interested peers
            HashSet<String> unchokedPeers = hostPeer.unchokedList;
            HashSet<String> interestedPeers = hostPeer.interList;

            //consider interested Peers
            if (interestedPeers.size() > 0) // if there are any peers interested
            {
                //peer has complete file


            } else //unchoke based on download rates
            {

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // stop the scheduler from periodically choking peers
    // will be called upon completion
    public void shutdown(){
        scheduler.shutdownNow();
    }
}
//=======
//import java.util.*;
////necessary libraries for scheduled execution choking peers
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//import static java.util.stream.Collectors.toMap;
//
//public class Choke implements Runnable{
//    public int chokingInterval;
//    public int maxNeighbors;
//    public Peer hostPeer;
//    public ScheduledExecutorService scheduler = null;
//
//    // choke constructor to initialize the choking interval and max neighbors
//    public Choke(Peer hostPeer)
//    {
//        this.hostPeer = hostPeer;
//        this.chokingInterval = hostPeer.peerConfig.getUnchokingInterval();
//        this.maxNeighbors = hostPeer.peerConfig.getNumberOfPreferredNeighbors();
//    }
//
//    public void chokePeriodically(){
//        // schedule the choking of peers
//        // modify initial delay if necessary
//        scheduler.scheduleAtFixedRate(this, 10, chokingInterval, TimeUnit.SECONDS);
//    }
//
//    // run method will periodically update the which peers are choked and unchoked
//    public void run() {
//        try {
//            // get the list of unchoked peers and interested peers
//            HashSet<String> unchokedPeers = hostPeer.unchokedList;
//            HashSet<String> interestedPeers = hostPeer.interList;
//
//            //consider interested Peers
//            if (interestedPeers.size() > 0) // if there are any peers interested
//            {
//                //peer has complete file
//
//
//            } else //unchoke based on download rates
//            {
//
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    // stop the scheduler from periodically choking peers
//    // will be called upon completion
//    public void shutdown(){
//        scheduler.shutdownNow();
//    }
//}
//>>>>>>> Stashed changes
