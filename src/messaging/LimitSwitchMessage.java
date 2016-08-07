package messaging;

/**
 * Created by USER on 2/20/2015.
 */
public class LimitSwitchMessage implements IMessage {
    private boolean isPressed = false;
    private byte limitSwitchId;

    public LimitSwitchMessage(byte[] data){
        assert(data.length == 2 + 2);

        int i = 2;
        limitSwitchId = data[i++];
        i = ++i;
        if (data[i++] == 0){
            isPressed = false;
        }
        else if (data[i++] == 1){
            isPressed = true;
        }
    }

    public boolean getIsPressed(){
        return isPressed;
    }

    public byte getLimitSwitchId() {
        return limitSwitchId;
    }

    public byte[] getBytes() {
        throw new UnsupportedOperationException();
    }
}
