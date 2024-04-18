

import java.util.Arrays;

public class PieceMessage {
    //for each peer, the pieces they have must be kept in their subdirectories
    public static int[] fromByteArrayToInt(byte[] bytes){
        //the piece message has a 4 byte piece index and then the piece content
        //therefore we have to read the first 4 bytes and covert them to an integer
        int[] values = new int[4]; // the piece index in an int
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(j == 0){
                    values[i] += (bytes[j] & 0xFF) << 24;
                }
                else if(j == 1){
                    values[i] += (bytes[j] & 0xFF) << 16;
                }
                else if(j == 2){
                    values[i] += (bytes[j] & 0xFF) << 8;
                }
                else{
                    values[i] += (bytes[j] & 0xFF);
                }
            }
        }
        return values;
    }
    //initial storage of pieces for each peer
    //so each peer must have pieces stored in a map
}
