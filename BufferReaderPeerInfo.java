import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
public class BufferReaderPeerInfo {

    public static PeerInfo reader() throws IOException {
        BufferedReader brIn = new BufferedReader(new FileReader("PeerInfo.cfg"));
        String line;
        String pId = null;
        String pAddress = null;
        String pPort = null;
        String hasfile = null;
        while ((line = brIn.readLine()) != null) {
            String[] peerInfo = line.split(" ");
            pId = peerInfo[0];
            pAddress = peerInfo[1];
            pPort = peerInfo[2];
            hasfile = peerInfo[3];
        }
        PeerInfo rpInfo = new PeerInfo(pId, pAddress, pPort, hasfile);
        return rpInfo;

    }

}
