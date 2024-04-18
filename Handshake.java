import java.nio.ByteBuffer;
import java.util.Arrays;

public class Handshake {
    /*
    Handshake message is 32 byte array
    18 byte Header: "P2PFILESHARINGPROJ"
    10 byte zeroes: "0000000000"
    4 byte PeerId: "1001"
     */

    byte[] handshakeByteArray = new byte[32];
    String peerId;
    final String HEADER = "P2PFILESHARINGPROJ";
    final int Header_length = 18;
    final int zeroes_length = 10;
    final int peer_id_length = 4;
    final int handshake_length = 32;

    //making two ctor's because I am unsure of the usage

    //construct a Handhsake message with the peerID
    public Handshake(String peerId) {
        this.peerId = peerId;

        // populate the first 18 bytes of the byte array with the header
        System.arraycopy(HEADER.getBytes(), 0, handshakeByteArray, 0, Header_length);

        //next 10 bytes will be zeroes
        Arrays.fill(handshakeByteArray,Header_length,Header_length + zeroes_length,(byte)0);

        //populate the last 4 bytes of the byte array with the peerId
        // Convert peerId to a byte array
        byte[] peerIdBytes = ByteBuffer.allocate(peer_id_length).putInt(Integer.parseInt(peerId)).array();
        System.arraycopy(peerIdBytes, 0, handshakeByteArray, 28, 4);

    }

    //construct a Handshake message from a byte array
    public Handshake(byte[] byteArray) {
        if (byteArray.length != 32) {
            throw new IllegalArgumentException("Invalid handshake message length.");
        }
        //validate the header
        String receivedHeader = new String(Arrays.copyOfRange(byteArray, 0, HEADER.length()));
        if (!HEADER.equals(receivedHeader)) {
            throw new IllegalArgumentException("Invalid handshake header.");
        }
        //extract the peerId
        ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(byteArray, 28, 32));
        this.peerId = String.valueOf(wrapped.getInt());

        this.handshakeByteArray = byteArray.clone();

    }
    //convert Handshake object to byte array to send over the network
    public byte[] toByteArray() {
        return handshakeByteArray.clone();
    }
    //Create a Handshake object from received byte array
    public static Handshake fromByteArray(byte[] byteArray) {
        return new Handshake(byteArray);
    }

    //for debugging purposes:
    @Override
    public String toString() {
        return "Handshake{" +
                "peerId=" + peerId +
                ", handshakeMessage=" + Arrays.toString(handshakeByteArray) +
                '}';
    }
}
