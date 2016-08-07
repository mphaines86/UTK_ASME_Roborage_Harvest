package messaging;

/**
 * Created by Michael Haines on 10/12/2015.
 */
public class KillMessage implements IMessage {

    int hardKill;

    public KillMessage(int hardKill){
        this.hardKill = hardKill;
    }
    public KillMessage(byte[] data){
        assert data.length == 3;

        int i = 2;
        this.hardKill = (int)data[++i];
    }


    public byte[] getBytes(){
        byte[] ret = new byte[]{
                0, 'k', (byte)this.hardKill,
        };

        ret[0] = (byte)ret.length;
        return ret;
    }

}
