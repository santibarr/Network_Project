import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.*;

public class PeerProcess {

    static String peerID;

    public static void main(String[] args) throws IOException {


        if (args.length == 0) {
            System.out.println("Failure: No peers have been started");
            return;
        }

        //if the cc args are being filled in
        peerID = args[0];
    }

}
