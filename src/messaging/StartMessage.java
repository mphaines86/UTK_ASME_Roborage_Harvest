package messaging;

/**
 * Created by michael on 9/5/16.
 */
public class StartMessage implements IMessage {

    byte time;
    byte start;

    public StartMessage(byte time, byte start){
        this.time = time;
        this.start = start;
    }
    public StartMessage(byte[] data){
        assert data.length == 4;

        int i = 2;
        this.time = data[++i];
        this.start = data[++i];
    }


    public byte[] getBytes(){
        byte[] ret = new byte[]{
                0, 's', this.time, this.start,
        };

        ret[0] = (byte)ret.length;
        return ret;
    }
}
