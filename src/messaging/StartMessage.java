package messaging;

/**
 * Created by michael on 9/5/16.
 */
public class StartMessage implements IMessage {

    byte setupBit;
    byte startBit;
    byte teams;

    public StartMessage(byte setupBit, byte start, byte teams){
        this.setupBit = setupBit;
        this.startBit = start;
        this.teams = teams;
    }

    public StartMessage(byte[] data){
        assert data.length == 4;

        int i = 1;
        this.setupBit = data[++i];
        this.startBit = data[++i];
        this.teams = data[++i];
    }


    public byte[] getBytes(){
        byte[] ret = new byte[]{
                0, 's', this.setupBit, this.startBit, this.teams
        };

        ret[0] = (byte)ret.length;
        return ret;
    }
}
