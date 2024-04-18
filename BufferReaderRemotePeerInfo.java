import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
public class BufferReaderRemotePeerInfo {
    public static SortedMap<String, RemotePeerInfo> peerMap; // keeps the peerIDs plus their PeerInfo
    public static Vector<String> allPeers; // keeps the peerIDs
    public static SortedMap<String, RemotePeerInfo> reader() throws IOException {
        peerMap = new TreeMap<>();
        allPeers = new Vector<>();
        BufferedReader brIn = new BufferedReader(new FileReader("PeerInfo.cfg"));
        String line;
        while ((line = brIn.readLine()) != null) {
            String[] peerInfo = line.split(" ");
            String pId = peerInfo[0];
            String pAddress = peerInfo[1];
            String pPort = peerInfo[2];
            String hasfile = peerInfo[3];
            RemotePeerInfo rpInfo = new RemotePeerInfo(pId, pAddress, pPort, hasfile);
            peerMap.put(peerInfo[0], rpInfo);
            allPeers.add(peerInfo[0]);
        }
        return peerMap;
    }

}
