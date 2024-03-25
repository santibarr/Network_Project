//Each bit in the bitfield payload represents whether the peer has the corresponding piece or not. The
//first byte of the bitfield corresponds to piece indices 0 – 7 from high bit to low bit,
//respectively. The next one corresponds to piece indices 8 – 15, etc. Spare bits at the end
//are set to zero. Peers that don’t have anything yet may skip a ‘bitfield’ message.

import java.io.IOException;
import java.util.*;

public class BitFieldMessage {
    // i want to convert the byte to integer to see the piece that the bitfield is representing
    //call the reader to see who has the complete file
    public static Map<String,byte[]> peerHasCompleteFile() throws IOException {
        Map<String,byte[]> mapWithBitfield = new HashMap<>();
        BufferReaderRemotePeerInfo peer = new BufferReaderRemotePeerInfo();
        SortedMap<String,RemotePeerInfo> peerMap = BufferReaderRemotePeerInfo.reader();
        int fileSize = BufferReaderCommonCfg.reader().getFileSize();
        int pieceSize = BufferReaderCommonCfg.reader().getPieceSize();
        int pieceNums = (int) Math.ceil((double)fileSize/pieceSize);

        byte[] storeBitField = new byte[pieceNums];

        //iterate through the map and find who has the complete file
        Iterator<Map.Entry<String, RemotePeerInfo>> iterator = peerMap.entrySet().iterator();

        while(iterator.hasNext()){
            Map.Entry<String, RemotePeerInfo> it = iterator.next(); // fill the bitfield woth 1 if it has the complete file
            if(Objects.equals(it.getValue().peerHasFile, "1")){
                for(int i = 0; i < pieceNums; i++){
                    storeBitField[i] = 1;
                }
                mapWithBitfield.put(it.getKey(), storeBitField);
            }
            else if(Objects.equals(it.getValue().peerHasFile, "0")){
                for(int i = 0; i < pieceNums; i++){
                    storeBitField[i] = 0; // fill the bitfield with 0 if it doesnt have the complete file
                }
                mapWithBitfield.put(it.getKey(), storeBitField);
            }
        }
        return mapWithBitfield;
    }
    public static int byteArrayToInt(byte[] bytes){
        int value = 0;
        for(int i = 0; i < 4; i++)
        {
            if(i == 0){
                value += (bytes[i] & 0xFF) << 24;
            }
            else if(i == 1){
                value += (bytes[i] & 0xFF) << 16;
            }
            else if(i == 2){
                value += (bytes[i] & 0xFF) << 8;
            }
            else{
                value += (bytes[i] & 0xFF);
            }
        }
        return value;
    }
    //must create a bitfield for the initial peer that has the complete file

}
