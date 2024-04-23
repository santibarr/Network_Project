import java.util.concurrent.*;

//Class is responsible for "Destroying" the peer aka shutting it down.
public class Destroy implements Runnable{
    public Peer peerHost;
    public ScheduledExecutorService service = null;

    //constructor
    Destroy(Peer p){
        this.peerHost = p;
        this.service = Executors.newScheduledThreadPool(1);
    }

    public void start(int timeInt){
        service.scheduleAtFixedRate(this,30,timeInt*2, TimeUnit.SECONDS);
    }
    public void run(){

        //if the peerHost has the complete, then stop all threads and the service being ran
        if(this.peerHost.checkCompleted()){
            this.peerHost.stopAllThreads();
            this.service.shutdownNow();
        }
    }


}
