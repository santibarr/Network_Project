import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.SortedMap;
import java.util.TreeMap;

/*
 * The StartRemotePeers.StartRemotePeers.StartRemotePeers.StartRemotePeers class begins remote peer processes.
 * It reads configuration file PeerInfo.cfg and starts remote peer processes.
 * You must modify this program a little bit if your peer processes are written in C or C++.
 * Please look at the lines below the comment saying IMPORTANT.
 */
public class StartRemotePeers {

	public Vector<RemotePeerInfo> peerInfoVector;
	
	public void getConfiguration() throws IOException
	{
		peerInfoVector = new Vector<RemotePeerInfo>();
		SortedMap<String, RemotePeerInfo> peerMap = new TreeMap<>();
		try {
			BufferedReader brIn = new BufferedReader(new FileReader("PeerInfo.cfg"));
			String line;

			// we are reading in the PeerInfo.cfg and storing it in a map in order of the peerID
			while ((line = brIn.readLine()) != null) {
				String[] peerInfo = line.split(" ");
				String pId = peerInfo[0];
				String pAddress = peerInfo[1];
				String pPort = peerInfo[2];
				RemotePeerInfo rpInfo = new RemotePeerInfo(pId, pAddress, pPort);
				peerMap.put(peerInfo[0], rpInfo);
				//We are also adding the Peer Info to a vector
				peerInfoVector.addElement(new RemotePeerInfo(peerInfo[0], peerInfo[1], peerInfo[2]));
			}
			brIn.close();
		} catch(Exception ex) {
			System.out.println(ex.toString());
		}
		//from the reader for Peer Info add them all to a vector
		/*String st;
		int i1;
		peerInfoVector = new Vector<RemotePeerInfo>();
		try {
			BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));
			while((st = in.readLine()) != null) {

				 String[] tokens = st.split("\\s+");
		    	 //System.out.println("tokens begin ----");
			     //for (int x=0; x<tokens.length; x++) {
			     //    System.out.println(tokens[x]);
			     //}
		         //System.out.println("tokens end ----");

			     peerInfoVector.addElement(new RemotePeerInfo(tokens[0], tokens[1], tokens[2]));

			}

			in.close();
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
		}*/
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			StartRemotePeers myStart = new StartRemotePeers();
			myStart.getConfiguration();
					
			// get current path
			String path = System.getProperty("user.dir");
			
			// start clients at remote hosts
			for (int i = 0; i < myStart.peerInfoVector.size(); i++) {
				RemotePeerInfo pInfo = (RemotePeerInfo) myStart.peerInfoVector.elementAt(i);
				
				System.out.println("Start remote peer " + pInfo.peerId +  " at " + pInfo.peerAddress );
				
				// *********************** IMPORTANT *************************** //
				// If your program is JAVA, use this line.
				Runtime.getRuntime().exec("ssh " + pInfo.peerAddress + " cd " + path + "; java peerProcess " + pInfo.peerId);
				
				// If your program is C/C++, use this line instead of the above line. 
				//Runtime.getRuntime().exec("ssh " + pInfo.peerAddress + " cd " + path + "; ./peerProcess " + pInfo.peerId);
			}		
			System.out.println("Starting all remote peers has done." );

		}
		catch (Exception ex) {
			System.out.println(ex);
		}
	}

}
