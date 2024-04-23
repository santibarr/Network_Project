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

    public int downloadrate = 0;
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
                    //find out the count for the true bitsets which are the pieces available
                    int count = 0;
                    for(int i = hostPeerBitfield.nextSetBit(0);i>=0; i = hostPeerBitfield.nextSetBit(i+1)){
                        count++;
                    }
                    if (hostPeer.peerInfo.peerHasFile.equals("1") || count > 0) {
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
                    while (inputStr.available() <4){ //wait until the input stream has at least 4 bytes
//                        try {
//                            Thread.sleep(100); // don't know if this is an appropriate time to wait
//                            // experiment with different times if necessary
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
                    //Here we process the code to read in the incoming message
                    //parse the input and get everything from the response

//                    byte[] msgLength = new byte[4]; // make buffer for message length
//                    int messageSize = inputStr.read(msgLength,0,4); //read the message length
                    //the first 4 bytes of the message is the length of the message
                    int msgSize = inputStr.readInt();
                    byte[] fullResponse = new byte[msgSize];
                    inputStr.readFully(fullResponse);
                    char type = (char) fullResponse[0]; //get the type of the message
                    byte[] messagePayload = new byte[msgSize - 1];
                    System.arraycopy(fullResponse, 1, messagePayload, 0, msgSize-1); //get the payload of the message
                    //based on the response we get, we will build a new message
                    Message msg = new Message(msgSize, type, messagePayload);

                    //sort everything into its place based on message type
                    if(type == '0') {
                        System.out.println("this is in case 0");
                        Choke();
                        break;
                    }
                    else if(type == '1'){
                        System.out.println("this is in case 1");
                        Unchoke();
                        break;
                    }
                    //interested section
                    else if(type == '2'){
                        System.out.println("this is in case 2");
                        Interested();
                        break;
                    }
                    //not interested section
                    else if(type == '3'){
                        System.out.println("this is in case 3");
                        notInterested();
                        break;
                    }
                    //have section
                    else if(type == '4'){
                        System.out.println("this is in case 4");
                        Have(msg);
                        break;
                    }
                    //bitfield section
                    else if(type == '5'){
                        System.out.println("this is in case 5");
                        Bitfield(msg);
                        break;
                    }
                    //request section
                    else if(type == '6'){
                        System.out.println("this is in case 6");
                        Request(msg);
                        break;
                    }
                    //piece section
                    else if(type == '7'){
                        System.out.println("this is in case 7");
                        Piece(msg);
                        break;
                    }
                    else{
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

    public void Choke(){

        //choke
        this.hostPeer.clearRequestTracker(this.otherPeerID);

        //log choking
        this.hostPeer.getLog().logChokedPeer(this.otherPeerID);
    }

    public void Unchoke(){

        //unchoke peer
        int request = this.hostPeer.checkRequested(this.otherPeerID);

        if(request < 0){
            this.notInterestedMsg();
        }
        else{
            this.requestMsg(request);
        }

        //log unchoke
        this.hostPeer.getLog().logUnchokedPeer(this.otherPeerID);
    }

    public void Interested(){
        //add other peer to host interested peer list
        this.hostPeer.addInterestedPeer(this.otherPeerID);

        //log interested
        this.hostPeer.getLog().logInterestedPeer(this.otherPeerID);
    }

    public void notInterested(){

        //remove other peer if hostPeer is not interested in them
        this.hostPeer.removeInterestedPeer(this.otherPeerID);

        //log notInterested
        this.hostPeer.getLog().logUninterestedPeer(this.otherPeerID);
    }

    public void Have(Message msg){
        int pieceIndex = msg.retrieveIndexFromMsg(msg.payload,0);
        this.hostPeer.updateBitfield(this.otherPeerID,pieceIndex);

        //check to see if we have everything done and stop the peer
        if(this.hostPeer.checkCompleted()){
            this.hostPeer.stopPeer();
        }

        //We want to see if we are still interested in the other peer
        if(this.hostPeer.interestCheck(this.otherPeerID)){
            this.interestedMsg();
            this.hostPeer.getLog().logInterestedPeer(this.otherPeerID);
        }
        else{
            this.notInterestedMsg();
            this.hostPeer.getLog().logUninterestedPeer(this.otherPeerID);
        }

        //log the message
        this.hostPeer.getLog().logHave(this.otherPeerID, pieceIndex);

    }

    public void Bitfield(Message msg){

        //get and update bitfield for the hostpeer
        BitSet bitset = msg.retrieveBitField();
        this.hostPeer.updateEntireBitfield(this.otherPeerID,bitset);

        //in response, we want to see if we are interested in the peer
        //log the response after we send the Msg, need to see if other peer has the file
        if(!this.hostPeer.checkCompleted()){
            if(this.hostPeer.peerConfig.fileSize == 1){
                this.interestedMsg();
                this.hostPeer.getLog().logInterestedPeer(this.otherPeerID);
            }
            else{
                this.notInterestedMsg();
                this.hostPeer.getLog().logUninterestedPeer(this.otherPeerID);
            }
        }
    }

    public void Request(Message msg){

        int pieceindex = 0;
        //we want to send a request if we meet these conditions:
        // the unchoked list has the other peer we are interested in and OptUnchokedPeer is there
        if(this.hostPeer.unchokedList.contains(this.otherPeerID) || (this.hostPeer.currOptUnchokedId != null && this.hostPeer.currOptUnchokedId.compareTo(this.otherPeerID) == 0)){
            pieceindex = msg.retrieveIndexFromMsg(msg.payload,0);
        }

        this.pieceMsg(pieceindex, msg.payload);
    }

    public void Piece(Message msg){
        try{

            //gather the information necessary
            int pieceIndex = msg.retrieveIndexFromMsg(msg.payload,0);
            byte[] pieceData = msg.retrievePiecePayload();

            //write to the file
            this.hostPeer.writePiece(pieceIndex, pieceData);
            this.downloadrate++;

            //log it
            this.hostPeer.getLog().logPieceReceived(this.otherPeerID, pieceIndex, this.hostPeer.numPieces);

            //send the have msg
            this.hostPeer.updateRequestTracker(pieceIndex, null);
            this.hostPeer.HaveMessage();



        }
        catch(Exception e){
            e.printStackTrace();
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

    public void requestMsg(int pieceBit){
        try{
            //build the request msg
            byte [] msg = ByteBuffer.allocate(4).putInt(pieceBit).array();
            Message newMsg = new Message('6',msg);
            this.outputStr.write(newMsg.writeMessage());
            this.outputStr.flush();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void pieceMsg(int pieceBit, byte[] payload){

        try{
            ByteArrayOutputStream str = new ByteArrayOutputStream();
            byte[] bytes = ByteBuffer.allocate(4).putInt(pieceBit).array();
            //construct the msg
            str.write(bytes);
            str.write(payload);
            Message newMsg = new Message('7', str.toByteArray());

            //write the msg to a file and send it
            this.outputStr.write(newMsg.writeMessage());
            this.outputStr.flush();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
