// Class to manage connecions between different peers

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*
    contains Socket
    Peer variable (local peer)
    other Peer's ID (as a string)
    Handshake
    Download rate
    input and output streams


    Will have all of the logic to have different behavior depending on the Message Type

    Will respond to different messages types accordingly (sending response messages)

 */
public class PeerConnection implements Runnable{
    private Socket socketConnection;
    private ObjectInputStream inputStr = null;
    private ObjectOutputStream outputStr = null;
    private String peerID;
    public String otherPeerID;
    public Handshake handshake;

    private boolean connected = false;

    public P2PLog logger;
    public Peer hostPeer;

    public PeerConnection(Peer hostPeer, Socket socketConnection){
        this.socketConnection = socketConnection;
        this.hostPeer = hostPeer;

        try{
            // Check if the socket is connected
            if (!socketConnection.isConnected()) {
                throw new IOException("Failed to establish connection with the server");
            }
            outputStr = new ObjectOutputStream(socketConnection.getOutputStream());
            outputStr.flush();
            inputStr = new ObjectInputStream(socketConnection.getInputStream());

        }
        catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    public void run() {
        try {

            //here we have to send the handshake
            byte[] handshakeByte;
            this.handshake = new Handshake(peerID);
            handshakeByte = this.handshake.MakeHandshake();
            outputStr.write(handshakeByte);
            outputStr.flush();

            if(!this.connected){
                // Wait for the handshake response
                byte[] returnedMessage = new byte[32];
                inputStr.readFully(returnedMessage);

                // Check the handshake response
                Handshake handshakeCheck = new Handshake(returnedMessage);
                this.otherPeerID = handshakeCheck.peerId;
            }
            else{

                //parse the input and get everything from the response
                int msgSize = inputStr.readInt();

                byte[] resp = new byte[msgSize];
                inputStr.readFully(resp);
                char type = (char) resp[0];

                //based on the response we get, we will build a new message
                Message msg = new Message(type);
                msg.readMessage(msgSize, resp);

                //sort everything into its place based on message type
                switch(type){
                    //choked message
                    case '0':
                        System.out.println("this is in case 0");
                        this.hostPeer.getLog().logChokedPeer(this.otherPeerID);
                        break;
                    //unchoked message
                    case '1':
                        System.out.println("this is in case 1");
                        break;
                    //interested
                    case '2':
                        System.out.println("this is in case 2");
                        break;
                    //not interested
                    case '3':
                        System.out.println("this is in case 3");
                        break;
                    //have
                    case '4':
                        System.out.println("this is in case 4");
                        break;
                    //bitfield
                    case '5':
                        System.out.println("this is in case 5");
                        break;
                    //request
                    case '6':
                        System.out.println("this is in case 6");
                        break;
                    //piece
                    case '7':
                        System.out.println("this is in case 7");
                        break;
                }

            }


            // Now that the handshake is complete, you can start the actual communication
            // For example, you can start by sending a 'have' message to the other peer
            // You can also start listening for incoming messages from the other peer

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
