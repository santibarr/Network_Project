// Class to manage connecions between different peers

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;

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
    public ObjectInputStream inputStr = null;
    public ObjectOutputStream outputStr = null;
    public String peerID;
    public String otherPeerID;


    public boolean hasEnteredConnection = false;
    public boolean start = false;

    public Peer hostPeer;

    public final String HEADER = "P2PFILESHARINGPROJ";
    public final int Header_length = 18;
    public final int zeroes_length = 10;
    public final int peer_id_length = 4;
    public final int handshake_length = 32;


    public PeerConnection(Peer hostPeer, Socket socketConnection){
        this.socketConnection = socketConnection;
        this.hostPeer = hostPeer;
        this.peerID = hostPeer.peerInfo.peerId;

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

    public PeerConnection(Peer hostPeer, Socket socketConnection, String otherPeerID){
        this.socketConnection = socketConnection;
        this.hostPeer = hostPeer;
        this.peerID = hostPeer.peerInfo.peerId;
        this.otherPeerID = otherPeerID;

        this.start = true; // change it to somewhere if this fails

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
            //create handshake message to be sent
            byte[] handshakeByte = new byte[32];
            ByteArrayOutputStream handshakeBuilder = new ByteArrayOutputStream();
            try {
                //write the header
                handshakeBuilder.write(HEADER.getBytes());
                //next 10 bytes are 0's
                byte[] zeroBytes = new byte[10];
                handshakeBuilder.write(zeroBytes);
                //write the peerId
                handshakeBuilder.write(this.peerID.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
            handshakeByte = handshakeBuilder.toByteArray();
            outputStr.write(handshakeByte);
            outputStr.flush();
            while(true) {
                if (this.hasEnteredConnection == false) {
                    // Wait for the handshake response
                    byte[] returnedMessage = new byte[32];
                    inputStr.readFully(returnedMessage);

                    //read in the message to see if the formatting is correct
                    String otherID = null;
                    if (returnedMessage.length != 32) {
                        throw new IllegalArgumentException("Invalid handshake message length.");
                    }
                    //validate the header
                    String receivedHeader = new String(Arrays.copyOfRange(returnedMessage, 0, HEADER.length()));
                    if (!HEADER.equals(receivedHeader)) {
                        throw new IllegalArgumentException("Invalid handshake header.");
                    }
                    //extract the peerId which is going to be the other peerID
                    byte[] peerIdBytes = Arrays.copyOfRange(returnedMessage, 28, 32);
                    otherID = new String(peerIdBytes);

                    //convert the otherPeerID to the peerID passed through
                    this.otherPeerID = otherID;
                    this.hostPeer.connectedPeers.put(this.otherPeerID, this);
                    this.hostPeer.threadMap.put(this.otherPeerID, Thread.currentThread());
                    this.hasEnteredConnection = true;

                    if (this.start == true) {
                        this.hostPeer.getLog().logTCPsend(this.otherPeerID);
                    } else {
                        this.hostPeer.getLog().logTCPreceive(this.otherPeerID);
                    }

                    // after establishing connection, send bitfield to other peer
                    BitSet hostPeerBitfield = hostPeer.bitfieldMap.get(hostPeer.peerInfo.peerId);
                    if (hostPeer.peerInfo.peerHasFile.equals("1") || hostPeerBitfield.cardinality() > 0) {
                        try {
                            Message bitfieldMsg = new Message('5', hostPeerBitfield.toByteArray());
                            // send the bitfield message
                            outputStr.write(bitfieldMsg.writeMessage());
                            outputStr.flush();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //process incoming messages
                    while (inputStr.available() < 4){ //wait until the input stream has at least 4 bytes
                        try {
                            Thread.sleep(100); // don't know if this is an appropriate time to wait
                            // experiment with different times if necessary
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //parse the input and get everything from the response
                    int msgSize = inputStr.readInt();

                    byte[] resp = new byte[msgSize];
                    inputStr.readFully(resp); //read the message from the input stream
                    char type = (char) resp[0]; //message type is the first byte

                    //based on the response we get, we will build a new message
                    Message msg = new Message(type);
                    msg.readMessage(msgSize, resp);

                    //sort everything into its place based on message type
                    switch (type) {
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
            }


            // Now that the handshake is complete, you can start the actual communication
            // For example, you can start by sending a 'have' message to the other peer
            // You can also start listening for incoming messages from the other peer

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //send the "have" message back to the peer: we need the piece index for that
    public void haveMsg(int index){

        try{
            //index
            byte[] msg = ByteBuffer.allocate(4).putInt(index).array();
            //craft the message
            Message newMsg = new Message('4', msg);
            this.outputStr.write(newMsg.writeMessage());
            this.outputStr.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //Write the choke message for the peer
    public void ChokeMsg(){

        try{
            //doesn't have a payload, so we need only the type
            Message newMsg = new Message('0');
            this.outputStr.write(newMsg.writeMessage());
            this.outputStr.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //Write the "unchoked" msg for the peer
    public void UnchokedMsg(){

        try{
            //doesn't have a payload, so we need only the type
            Message newMsg = new Message('1');
            this.outputStr.write(newMsg.writeMessage());
            this.outputStr.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //Write the "interested" msg for the peer
    public void interestedMsg(){

        try{
            //doesn't have a payload, so we need only the type
            Message newMsg = new Message('2');
            this.outputStr.write(newMsg.writeMessage());
            this.outputStr.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //Write the "not interested" msg for the peer
    public void notInterestedMsg(){

        try{
            //doesn't have a payload, so we need only the type
            Message newMsg = new Message('3');
            this.outputStr.write(newMsg.writeMessage());
            this.outputStr.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
