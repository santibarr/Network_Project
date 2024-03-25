import java.nio.ByteBuffer;
import java.util.Arrays;


public class Message {
    /*
    Message consists of 3 parts
    4 byte Message Length
    1 byte Type of Message (mapped with MessageType enum)
    variable size Payload
     */

    int messageLength;
    byte messageType;
    byte[] payload;

    //additional information about source and destination of the message
    int sourcePeerID;
    int destinationPeerID;

    // Enum to map message type to byte representation:
    public enum MessageType {
        CHOKE(0),
        UNCHOKE(1),
        INTERESTED(2),
        NOT_INTERESTED(3),
        HAVE(4),
        BITFIELD(5),
        REQUEST(6),
        PIECE(7);

        //singular byte representation of the messageType
        byte value;

        MessageType(int value) {
            this.value = (byte) value;
        }

        public byte getValue() {
            return this.value;
        }

        public static MessageType fromByte(byte b) {
            for (MessageType mt : MessageType.values()) {
                if (mt.getValue() == b) {
                    return mt;
                }
            }
            throw new IllegalArgumentException("Unknown message type byte: " + b);
        }
    }

    //constructor to parse a byte array to a Message object
    public Message(byte[] byteArray, int src, int dest) {
        if (byteArray.length < 5) { //minimum length is 5 bytes (length + type)
            throw new IllegalArgumentException("Invalid message format");
        }
        sourcePeerID = src;
        destinationPeerID = dest;

        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        this.messageLength = buffer.getInt(); //first 4 bytes for length
        this.messageType = buffer.get(); //1 byte for type

        //don't know if this check is necessary
//        if (byteArray.length - 5 != messageLength - 1) {
//            throw new IllegalArgumentException("Mismatch between message length and payload size");
//        }

        this.payload = new byte[this.messageLength - 1];
        if (this.payload.length > 0) {
            buffer.get(this.payload, 0, this.payload.length);
        }
    }

    // Method to serialize the Message object into a byte array
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(4 + messageLength);
        buffer.putInt(messageLength);
        buffer.put(messageType);
        buffer.put(payload);
        return buffer.array();
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageLength=" + messageLength +
                ", messageType=" + MessageType.fromByte(messageType) +
                ", payload=" + Arrays.toString(payload) +
                '}';
    }

    public void HandleMessage(Message message)
    {
        switch(message.messageType)
        {
            case 0:
                System.out.println("Choke");
            case 1:
                System.out.println("Unchoke");
            case 2:
                System.out.println("Interested");
            case 3:
                System.out.println("Not Interested");
            case 4:
                System.out.println("Have");
            case 5:
                System.out.println("Bitfield");
            case 6:
                System.out.println("Request");
            case 7:
                System.out.println("Piece");
        }
    }

}
