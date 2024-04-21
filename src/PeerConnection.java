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
    public Socket socketConnection;
    public ObjectInputStream inputStr;
    public ObjectOutputStream outputStr;
    public String peerID;
    public String otherPeerID;
    public Handshake handshake;

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
            byte[] handshakeByte = new byte[32];
            this.handshake = new Handshake(peerID);
            handshakeByte = this.handshake.toByteArray();
            outputStr.write(handshakeByte);
            outputStr.flush();

            // Wait for the handshake response
            byte[] returnedMessage = new byte[32];
            inputStr.readFully(returnedMessage);

            // Check the handshake response
            Handshake handshakeCheck = new Handshake(returnedMessage);
            this.otherPeerID = handshakeCheck.peerId;

            // Log the successful TCP connection
            logger.logTCPreceive(this.otherPeerID);

            // Now that the handshake is complete, you can start the actual communication
            // For example, you can start by sending a 'have' message to the other peer
            // You can also start listening for incoming messages from the other peer

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
