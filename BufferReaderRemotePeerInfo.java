import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;
public class BufferReaderRemotePeerInfo {
    public static SortedMap<String, RemotePeerInfo> reader() throws IOException {
        SortedMap<String, RemotePeerInfo> peerMap = new TreeMap<>();
        BufferedReader brIn = new BufferedReader(new FileReader("PeerInfo.cfg"));
        String line;
        while ((line = brIn.readLine()) != null) {
            String[] peerInfo = line.split(" ");
            String pId = peerInfo[0];
            String pAddress = peerInfo[1];
            String pPort = peerInfo[2];
            RemotePeerInfo rpInfo = new RemotePeerInfo(pId, pAddress, pPort);
            peerMap.put(peerInfo[0], rpInfo);
        }
        return peerMap;
    }

}
