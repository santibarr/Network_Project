//Each bit in the bitfield payload represents whether the peer has the corresponding piece or not. The
//first byte of the bitfield corresponds to piece indices 0 – 7 from high bit to low bit,
//respectively. The next one corresponds to piece indices 8 – 15, etc. Spare bits at the end
//are set to zero. Peers that don’t have anything yet may skip a ‘bitfield’ message.

public class BitFieldMessage {
    // i want to convert the byte to integer to see the piece that the bitfield is representing
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
}
