

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;


public class Message {
    /*
    Messages.Message consists of 3 parts
    4 byte Messages.Message Length
    1 byte of Message Type
    variable size Payload
     */


    //message length
    public int length;

    //message type: 8 values
    public char type;

    //payload byte array
    public byte[] payload;

    //we are going to have overloaded Message types
    //This Message will contain: choke, unchoked, interested and uninterested
    Message(char type){

        this.length = 1;
        this.type = type;
        this.payload = new byte[0];
    }

    Message(char type, byte[] payload){

        this.length = payload.length + 1;
        this.type = type;
        this.payload = payload;
    }

    //write a new message to send to peers
    public byte[] writeMessage() {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //create new output message
        try{
            byte[] newLength = ByteBuffer.allocate(4).putInt(this.length).array();
            output.write(newLength);
            output.write((byte) this.type);
            output.write(this.payload);
           // output.flush();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return output.toByteArray();
    }

    //read in the message
    public void readMessage(int len, byte[] payload, char type){

        this.length = len;
        this.type = type;

        //get the payload from message
        byte[] temp = new byte[payload.length];
        System.arraycopy(payload, 1, temp, 0, this.payload.length);
        this.payload = temp;
    }

    //send this new bitfield back out for peers
    public BitSet retrieveBitField(){
        BitSet bits = new BitSet();
        bits = BitSet.valueOf(this.payload);
        return bits;
    }

    public char getType(){
        return this.type;
    }
}