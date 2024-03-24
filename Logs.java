import java.io.IOException;
import java.util.logging.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logs {
    private static Logger logger = Logger.getLogger(Logs.class.getName());

    //convenient formatting for logging messages
    public static class newFormat extends Formatter{

        private static String pattern = "yyyy-MM-dd HH:mm:ss";

        @Override
        public String format(LogRecord rec){
            SimpleDateFormat simpledate = new SimpleDateFormat(pattern);
            String formatDate = simpledate.format(new Date(rec.getMillis()));
            return String.format("%s %s %s%n", formatDate, rec.getLevel(), formatMessage(rec));
        }
    }

    //logs the message
    public static void logMsg(String msg){
        logger.info(msg);
    }

    //initialize new loggings when restarting P2P network
    public static void initPeerID(String PeerID){

        logger = Logger.getLogger("Logger");
        FileHandler fileHand;

        try{
            Handler[] handlers = logger.getHandlers();

            //remove previous handlers
            for(Handler handler: handlers){
                logger.removeHandler(handler);
            }

            logger.setUseParentHandlers(false);
            fileHand = new FileHandler(PeerID + ".log");
            logger.addHandler(fileHand);
            fileHand.setFormatter(new newFormat());
        } catch (SecurityException | IOException e){
            e.printStackTrace();
        }

    }
}
