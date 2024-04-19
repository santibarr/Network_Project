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
    private ObjectInputStream inputStr;
    private ObjectOutputStream outputStr;
    private String peerID;
    private String peerIDother;
    private Handshake handshake;
    public void run() {
        try {
            outputStr = new ObjectOutputStream(socketConnection.getOutputStream());
            outputStr.flush();
            inputStr = new ObjectInputStream(socketConnection.getInputStream());

            //here we have to send the handshake
            byte[] handshakeByte = new byte[32];
            this.handshake = new Handshake(peerID);
            handshakeByte = this.handshake.toByteArray();
            outputStr.write(handshakeByte);
            outputStr.flush();

            while (true){
                byte[] returnedMessage = new byte[32];
                inputStr.readFully(returnedMessage);
                //now we have to check that the handshake is correct
                Handshake handshakeCheck = new Handshake(returnedMessage);
                this.peerIDother = handshakeCheck.peerId;
                //add it to the logger
                break;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
