
import java.io.Serializable;


public class Message implements Serializable{
    private long channelID;
    private long timestamp, 
            ttl=2000; 
    private Object data; 

    
    public Message(long channelID, long timestamp, Object data) {
        this.channelID = channelID;
        this.timestamp = timestamp;
        this.data = data;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getchannelID() {
        return channelID;
    }

    public Object getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getTtl() {
        return ttl;
    }
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
    
    public void setchannelID(long channelID) {
        this.channelID = channelID;
    }
    
}
