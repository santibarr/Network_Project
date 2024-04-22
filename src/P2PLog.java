import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class P2PLog {

    private String logFile; //log file name

    private FileHandler logHandler = null; //can handle File handling
    private SimpleDateFormat dateTimeFormatter = null; //takes care of date formatting when logging
    private String peerId; //peerID

    private Logger logger = null; //set the logger to log messages

    //We get the ID of the peer and we kick off the logging process for that peer
    public P2PLog(String id){
        this.peerId = id;
        startup();
    }
    private void startup(){
        try {
            this.logFile = "log_peer_" + this.peerId + ".log";
            //FileHandler
            this.logHandler = new FileHandler(this.logFile, false);
            //set dates for when logging occurred
            this.dateTimeFormatter = new SimpleDateFormat("dd-mm-yyyy HH:MM:SS");
            //Set the logger
            this.logHandler.setFormatter(new SimpleFormatter());
            this.logger = Logger.getLogger("PeerLogs");

            this.logger.setUseParentHandlers(false);//independent logger
            this.logger.addHandler(this.logHandler);

        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    //log when Peer sends TCP message
    public synchronized void logTCPsend(String peer){
        String message = String.format("%s: Peer %s makes a connection to %s", getcurrentTime(), this.peerId, peer);
        this.logger.log(Level.INFO, message);
    }

    //log when peer receives TCP message
    public synchronized void logTCPreceive(String peer){
        String message = String.format("%s: Peer %s is receiving a connection from %s", getcurrentTime(), this.peerId,peer);
        this.logger.log(Level.INFO, message);
    }

    /*
    Log the seven states of messaging (exclude bitfield), and the neighbors that the peer is connected to.
    Log the preferred neighbors of a peer
    Log when there is an unoptimiscally unchoked neighbor
    */

    //log when peer is choked
    public synchronized void logChokedPeer(String peer){
        String message = String.format("%s: Peer %s choked peer %s", getcurrentTime(), this.peerId, peer);
        this.logger.log(Level.INFO, message);
    }

    //log when peer is unchoked
    public synchronized void logUnchokedPeer(String peer){
        String message = String.format("%s: Peer %s unchoked peer %s", getcurrentTime(), this.peerId, peer);
        this.logger.log(Level.INFO, message);
    }

    //log interested neighbors
    public synchronized void logInterestedPeer(String peer){
        String message = String.format("%s: Peer %s is interested in peer %s", getcurrentTime(), this.peerId, peer);
        this.logger.log(Level.INFO, message);
    }

    //log uninterested neighbors
    public synchronized void logUninterestedPeer(String peer){
        String message = String.format("%s: Peer %s is not interested in peer %s", getcurrentTime(), this.peerId, peer);
        this.logger.log(Level.INFO, message);
    }

    //log if peer has a piece, index parameter is meant for seeing what piece in particular
    public synchronized void logHave(String peer, int index){
        String message = String.format("%s: Peer %s received 'have' message from %s for piece %s", getcurrentTime(), this.peerId, peer, index);
        this.logger.log(Level.INFO, message);
    }

    //Log if peer requests a piece
    public synchronized void logRequest(String peer, int index){
        String message = String.format("%s: Peer %s received 'request' message from %s for piece %s", getcurrentTime(), this.peerId, peer, index);
        this.logger.log(Level.INFO, message);
    }

    //Log for Piece message: index param is meant for seeing what piece in particular, content param shows actual content
    public synchronized void logPieceReceived(String peer, int index, int content){
        String message = String.format("%s: Peer %s received piece from  peer %s for piece %s: content is %s", getcurrentTime(), this.peerId, peer, index, content);
        this.logger.log(Level.INFO, message);
    }

    //log the preferred neighbors of a peer
    public synchronized void logPrefNeighbors(List<String> neighbors){

        String neighborsStr = "";

        for(String neighbor : neighbors){
            neighborsStr = neighbor + ", ";
        }
        String message = String.format("%s: Peer %s has preferred neighbors: %s", getcurrentTime(), this.peerId, neighborsStr);
        this.logger.log(Level.INFO, message);
    }

    //log when we want to optimistically unchoke a neighbor (peer)
    public synchronized void logOptimisticallyUnchokedPeer(String peer){
        String message = String.format("%s: Peer %s is optimistically unchoking peer %s", getcurrentTime(), this.peerId, peer);
        this.logger.log(Level.INFO, message);
    }

    //shut down the logger
    public synchronized void shutdownLogger(){
        //if there is stuff written to the handler, close it
        if(this.logHandler != null){
            this.logHandler.close();
        }
    }

    //Helper method that gets the current time
    private synchronized String getcurrentTime(){

        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd-mm-yyy HH:MM:SS");
        return LocalDateTime.now().format(date);
    }



}
