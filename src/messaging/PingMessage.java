package messaging;

/**
 * Created by Michael Haines on 2/4/2016.
 */
public class PingMessage implements IMessage {
    int ping;

    public PingMessage(int ping){
        this.ping = ping;
    }
    public PingMessage(byte[] data){
        assert data.length == 3;

        int i = 1;
        this.ping = (int)data[++i];
    }


    public byte[] getBytes(){
        byte[] ret = new byte[]{
                0, 'p', (byte)this.ping,
        };

        ret[0] = (byte)ret.length;
        return ret;
    }
    public int getPing(){
        return ping;
    }
}
