import java.io.*;

public class PeerProcess {

    public static void main(String[] args) throws IOException {
        // command line argument corresponds to peerId
        String peerId = args[0];

        new Peer(peerId);
    }

}
