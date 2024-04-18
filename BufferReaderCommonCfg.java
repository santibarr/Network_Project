import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
class CommonCfgObject {
    //This class is made to read the Common.cfg file and encapsulate everything into an object
    private int numberOfPreferredNeighbors;
    private int unchokingInterval; // in seconds
    private int optimisticUnchokingInterval; // in seconds
    private String fileName;
    private int fileSize; // file size is in bytes
    private int pieceSize; // piece size is in bytes

    public CommonCfgObject(int numberOfPreferredNeighbors, int unchokingInterval, int optimisticUnchokingInterval,
                                 String fileName, int fileSize, int pieceSize)
    {
        this.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
        this.unchokingInterval = unchokingInterval;
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.pieceSize = pieceSize;
    }
    public int getNumberOfPreferredNeighbors() {
        return numberOfPreferredNeighbors;
    }
    public int getUnchokingInterval() {
        return unchokingInterval;
    }
    public int getOptimisticUnchokingInterval() {
        return optimisticUnchokingInterval;
    }
    public String getFileName() {
        return fileName;
    }
    public int getFileSize() {
        return fileSize;
    }
    public int getPieceSize() {
        return pieceSize;
    }
}
public class BufferReaderCommonCfg{
    public static CommonCfgObject reader() throws IOException
    {
        int numberOfPreferredNeighbors = 0;
        int unchokingInterval = 0;
        int optimisticUnchokingInterval = 0;
        String fileName = "";
        int fileSize = 0;
        int pieceSize = 0;
        //Reads in the file Common.cfg
        BufferedReader brIn = new BufferedReader(new FileReader("Common.cfg"));
        String line;
        //reads line by line of the file Common.cfg
        while((line = brIn.readLine()) != null)
        {
            //this assigns the appropriate information to the variable in the object
            String[] nameAndinfo = line.split(" ");
            if(nameAndinfo.length != 2) // it must only contain the name and the information
            {
                continue;
            }
            if(nameAndinfo[0].equals("NumberOfPreferredNeighbors")){
                numberOfPreferredNeighbors = Integer.parseInt(nameAndinfo[1]);
            }
            else if(nameAndinfo[0].equals("UnchokingInterval")) {
                unchokingInterval= Integer.parseInt(nameAndinfo[1]);
            }
            else if(nameAndinfo[0].equals("OptimisticUnchokingInterval")){
                optimisticUnchokingInterval= Integer.parseInt(nameAndinfo[1]);
            }
            else if(nameAndinfo[0].equals("FileName")){
                fileName = nameAndinfo[1];
            }
            else if(nameAndinfo[0].equals("FileSize")){
                fileSize = Integer.parseInt(nameAndinfo[1]);
            }
            else if(nameAndinfo[0].equals("PieceSize")){
                pieceSize = Integer.parseInt(nameAndinfo[1]);
            }
            else{
                continue;
            }
        }
        brIn.close();
        return new CommonCfgObject(numberOfPreferredNeighbors, unchokingInterval, optimisticUnchokingInterval, fileName, fileSize, pieceSize);
    }
}
