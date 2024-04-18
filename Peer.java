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

    public HashSet<String> connectedPeers;

    // constructor to initialize the peerObject.
    public Peer () throws IOException {

        //read in config files and peer info files

        this.peerConfig = BufferReaderCommonCfg.reader();
        this.peerInfo = BufferReaderPeerInfo.reader();

        // set up additional structures that will be maintained by the peer
        this.connectedPeers = new HashSet<>();
        //... additional variables.

    }

    public static void main(String[] args) throws IOException {
        Peer peer = new Peer();
        System.out.println(peer.peerConfig.getFileName());
        //System.out.println(peer.peerInfo.getPeerId());
    }
}
