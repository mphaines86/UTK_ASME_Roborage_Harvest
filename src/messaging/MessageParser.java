package messaging;

/**
 * Created by Michael Haines and Tanner Hobson 2/10/2015.
 */
public class MessageParser {

    static public IMessage parse(byte[] data){
        byte action = data[1];

        switch ((char)action){
            case 'm': return new MotorMessage(data);
            case 'b': return new BatteryMessage(data);
            case 'l': return new LimitSwitchMessage(data);
            case 'e': return new EncoderMessage(data);
            case 'p': return new PingMessage(data);
            default: return null;
        }
    }
}
