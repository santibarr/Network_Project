

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Handshake {
    /*
    Messages.Handshake message is 32 byte array
    18 byte Header: "P2PFILESHARINGPROJ"
    10 byte zeroes: "0000000000"
    4 byte PeerId: "1001"
     */

    byte[] handshakeByteArray = new byte[32];
    String peerId;
    final String HEADER = "P2PFILESHARINGPROJ";

    //making two ctor's because I am unsure of the usage

    //construct a Handhsake message with the peerID
    public Handshake(String peerId) {
        this.peerId = peerId;
        this.handshakeByteArray = MakeHandshake();
    }

    // find peerId from the handshake message
    public Handshake(byte[] handshakeByteArray) {
        this.handshakeByteArray = handshakeByteArray;
        String temp = new String(handshakeByteArray, StandardCharsets.UTF_8);
        this.peerId = temp.substring(28,32); //last 4 bytes correspond to the peerId's
    }

    //construct a Handshake byte array
    public byte[] MakeHandshake() {
        ByteArrayOutputStream handshakeBuilder = new ByteArrayOutputStream();
        try {
            //write the header
            handshakeBuilder.write(HEADER.getBytes(StandardCharsets.UTF_8));
            //next 10 bytes are 0's
            handshakeBuilder.write(new byte[10]);
            //write the peerId
            handshakeBuilder.write(peerId.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return handshakeBuilder.toByteArray();
    }
}