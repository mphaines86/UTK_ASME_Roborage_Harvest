package messaging;

/**
 * Created by Michael Haines on 2/21/2015.
 */
public class EncoderMessage implements IMessage{
    private byte encoderMessageId;
    private byte mostSignificantBit;
    private byte leastSignificantBit;

    public EncoderMessage(byte[] data){
        assert(data.length == 2 + 3);

        int i = 2;
        encoderMessageId = data[++i];
        //i = ++i;
        mostSignificantBit = data[++i];
        //i = ++i;
        leastSignificantBit = data[++i];
    }

    public byte getEncoderMessageId(){
        return encoderMessageId;
    }

    public byte getMostSignificantBit(){
        return mostSignificantBit;
    }

    public byte getLeastSignificantBit(){
        return leastSignificantBit;
    }

    public byte[] getBytes(){
        throw new UnsupportedOperationException();
    }
}
