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
    int peerId;
    final String HEADER = "P2PFILESHARINGPROJ";

    //making two ctor's because I am unsure of the usage

    //construct a Handhsake message with the peerID
    public Handshake(int peerId) {
        this.peerId = peerId;

        // populate the first 18 bytes of the byte array with the header
        System.arraycopy(HEADER.getBytes(), 0, handshakeByteArray, 0, HEADER.length());

        //next 10 bytes will be zeroes by default

        //populate the last 4 bytes of the byte array with the peerId
        // Convert peerId to a byte array
        byte[] peerIdBytes = ByteBuffer.allocate(4).putInt(peerId).array();
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
        this.peerId = wrapped.getInt();

        this.handshakeByteArray = byteArray.clone();
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
