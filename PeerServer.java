import java.io.IOException;
import java.net.ServerSocket;


public class PeerServer implements Runnable{
    //class to include the server and socket that the peer will use
    public ServerSocket peerSocket;
    public Thread serverThread;

    Peer peer;

    public PeerServer(PeerInfo peerInfo, CommonCfgObject peerConfig) throws IOException {
        //constructor for the PeerServer class
        //creates a new server and socket for the peer
        peerSocket = new ServerSocket (Integer.parseInt(peerInfo.peerPort));
    }

    public void run() {
        // while true, the server will accept incoming connections
        while (true){
            try {
                //accepts the incoming connection
                peerSocket.accept();

                //additional code to crete thread and Peer Handler

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
