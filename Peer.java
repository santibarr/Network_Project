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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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

    int numPieces; // number of pieces in the file
    String requestTracker[]; // tracks which pieces have been requested already.
    // example usage:
    // if this Peer requests piece 10 to Peer 1006, then requestTracker[10] = "1006"
    // this denotes the request piece 10 was made to Peer 1006.
    // if an index is null, then that piece has not been requested yet.

    public HashSet<String> peersInNetwork; // set of all peers (peerId's) in the network
    public HashSet<String> connectedPeers; // set of connected peers

    boolean finished;

    // constructor to initialize the peerObject.
    public Peer (String pId) throws IOException {
        //read in config files and peer info files
        this.peerConfig = BufferReaderCommonCfg.reader();
        this.peerInfo = BufferReaderPeerInfo.reader().get(pId);
        this.peersInNetwork = new HashSet<>(BufferReaderPeerInfo.allPeers);

        // set up additional structures that will be maintained by the peer
        this.connectedPeers = new HashSet<>();
        this.numPieces = (int) Math.ceil((double) this.peerConfig.getFileSize() / this.peerConfig.getPieceSize());
        this.requestTracker = new String[this.numPieces]; // each index corresponds to a piece.

        this.finished = false;
    }

    public void setUpPeer() throws IOException {
        //set up file and directory
        String filePath = "peer_" + this.peerInfo.peerId;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(filePath + "/" + this.peerConfig.getFileName());
        if (!file.exists()) {
            file.createNewFile();
        }

    }

    public static void main(String[] args) throws IOException {
        Peer peer = new Peer();
        System.out.println(peer.peerConfig.getFileName());
        //System.out.println(peer.peerInfo.getPeerId());
    }
}
