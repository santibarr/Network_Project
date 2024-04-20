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

    public HashSet<String> peersInNetwork; // set of all peers (peerId's) in the network
    public HashMap<String, PeerConnection> connectedPeers; // map of connected peers
    // key: peerId, value: PeerConnection object.
    // Each Peer will have n-1 PeerConnections (where n is the number of peers in the network)

    public RandomAccessFile fileBuilder; // file object to read and write the file
    // RandomAccessFile allows writes and reads to be made at any position in the file

    //networking variables
    public PeerServer peerServer;

    boolean finished;

    // constructor to initialize the peerObject.
    public Peer (String pId) throws IOException {
        //read in config files and peer info files
        this.peerConfig = BufferReaderCommonCfg.reader();
        this.peerInfo = BufferReaderPeerInfo.reader().get(pId);
        this.peersInNetwork = new HashSet<>(BufferReaderPeerInfo.allPeers);

        // set up additional structures that will be maintained by the peer
        this.connectedPeers = new HashMap<>();
        this.numPieces = (int) Math.ceil((double) this.peerConfig.getFileSize() / this.peerConfig.getPieceSize());
        this.requestTracker = new String[this.numPieces]; // each index corresponds to a piece.
        this.fileBuilder = null;
        this.peerServer = null;


        this.finished = false;

        setUpPeer();
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
        fileBuilder = new RandomAccessFile(file, "rw");
        // file should be the same length as the original file
        fileBuilder.setLength(peerConfig.getFileSize());

        //initialize the bitfield
        //make a helper method to fill our peer's bitfield

        //initialize the server
        peerServer = new PeerServer(peerInfo, peerConfig);

        //connect to the neighbors
        System.out.println("Trying to connect to peers...");
        while (connectedPeers.size() < 2) { //trying to get a connection between two different peers right not
            //DELETE ^ Later
            try {
                Thread.sleep(5000); //temporary sleep to allow all peers to start
                for (String peerIdInNetwork : peersInNetwork) {
                    if (!peerIdInNetwork.equals(peerInfo.peerId)) {
                        //skip connection to self:

                        // establish connection with each peer in the network
                        // Each Peer will have n-1 PeerConnections (where n is the number of peers in the network)
                        Socket interPeerSocket = new Socket(peerInfo.peerAddress, Integer.parseInt(peerInfo.peerPort));
                        PeerConnection peerConnection = new PeerConnection(this, interPeerSocket);

                        // set the other peer's ID
                        peerConnection.otherPeerID = peerIdInNetwork; // make sure that this peerId is the
                        // add the peerConnection to the connectedPeers set
                        connectedPeers.put(peerIdInNetwork, peerConnection);
                        // start the thread for the peerConnection
                        // a thread must be started for each peer connection to run in parallel
                        Thread peerConnectionThread = new Thread(peerConnection);
                        peerConnectionThread.start();

                        System.out.println("Peer " + peerInfo.peerId + " is connected to Peer " + peerIdInNetwork);

                        // DON'T CONTINUE UNTIL WE FIGURE OUT THE CONNECTION BETWEEN PEERS LOCALLY
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

//    public static void main(String[] args) throws IOException {
//        Peer peer = new Peer("1001");
//        peer.setUpPeer();
//        System.out.println(peer.peerConfig.getFileName());
//        //System.out.println(peer.peerInfo.getPeerId());
//    }
}


