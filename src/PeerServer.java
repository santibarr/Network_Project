import java.io.IOException;
import java.net.*;


public class PeerServer implements Runnable{
    //class to include the server and socket that the peer will use
    public ServerSocket peerSocket;
    public Thread serverThread;

    PeerConnection connection;
    Peer peer;

    public PeerServer(PeerInfo peerInfo, Peer peer) throws IOException {
        //constructor for the PeerServer class
        //creates a new server and socket for the peer
        peerSocket = new ServerSocket (Integer.parseInt(peerInfo.peerPort));
        this.peer = peer;


        System.out.println("Peer " + peerInfo.peerId + " is listening on port " + peerInfo.peerPort);
    }

    public void run() {
        // while true, the server will accept incoming connections
        while (true){
            try {

                //accepts the incoming connection
                Socket peerSocket = this.peerSocket.accept();

                //additional code to crete thread and Peer Handler
               PeerConnection peerConn =  new PeerConnection(this.peer,peerSocket);

               new Thread(peerConn).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
