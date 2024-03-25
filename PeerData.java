import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
public class PeerData {

    public boolean interested;
    private boolean optimallyUnchoked;
    private boolean preferred;

    private boolean choked;

    public PeerData(){

        this.choked = true;
        this.interested = false;
        this.optimallyUnchoked = false;
        this.preferred = false;
    }

    public boolean isPreferred(){
        return preferred;
    }

    //when our peers are not preferred or are unchoked
    public boolean isChoked(){
        return !(preferred || optimallyUnchoked);
    }

    public boolean isUnchoked(){
        return preferred || optimallyUnchoked;
    }

    public boolean isInterested(){
        return interested;
    }

    public boolean isOptimallyUnchoked() {
        return optimallyUnchoked;
    }

    public void choke(){
        this.optimallyUnchoked = false;
        this.preferred = false;
    }

    public void setPreferred(){
        this.preferred = true;
    }

    public void setOptimallyUnchoked() {
        this.optimallyUnchoked = true;
    }

    public void choking(){
        this.choked = true;
    }

    public void notChoking(){
        this.choked = false;
    }

    //check the all the peers have the bitfield filled with ones which means that they all have the complete file
    public static boolean canTerminate() throws IOException {
        boolean canEnd = true;
        //check the map with all the peers and their bitfields
        Map<String, byte[]> allBitfields = new HashMap<>();
        allBitfields = BitFieldMessage.peerHasCompleteFile();
        Iterator<Map.Entry<String, byte[]>> iterator = allBitfields.entrySet().iterator();
        int fileSize = BufferReaderCommonCfg.reader().getFileSize();
        int pieceSize = BufferReaderCommonCfg.reader().getPieceSize();
        int pieceNums = (int) Math.ceil((double)fileSize/pieceSize);
        while(iterator.hasNext()){
            Map.Entry<String, byte[]> it = iterator.next();
            byte[] bitField = it.getValue();
            for(int i = 0; i < pieceNums; i++){
                if(bitField[i] == 0){
                    canEnd = false;
                }
            }
        }
        return canEnd;
    }
    
}
