import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class P2PLog {

    private String logFile; //log file name

    private FileHandler logHandler = null; //can handle File handling
    private SimpleDateFormat dateTimeFormatter = null; //takes care of date formatting when logging
    private String peerId; //peerID

    private Logger logger = null; //set the logger to log messages

    //We get the ID of the peer and we kick off the logging process for that peer
    public void setPeerLogging(String id){
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
        String message = String.format("%s: Peer %s is making a connection to %s", getcurrentTime(), this.peerId, peer);
        this.logger.log(Level.INFO, message);
    }

    //log when peer receives TCP message
    public synchronized void logTCPreceive(String peer){
        String message = String.format("%s: Peer %s is receiving a connection from %s", getcurrentTime(), this.peerId, peer);
        this.logger.log(Level.INFO, message);
    }



    //gets the current time
    private synchronized String getcurrentTime(){

        DateTimeFormatter date = DateTimeFormatter.ofPattern("dd-mm-yyy HH:MM:SS");
        return LocalDateTime.now().format(date);
    }

}
