import java.util.ArrayList;
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
    
}
