package messaging;

/**
 * Created by Michael Haines and Tanner Hobson on 2/19/2015.
 */
public class BatteryMessage implements IMessage {
    private double voltage;
    private static int MAX_VOLTAGE = 16;

    public BatteryMessage(byte[] data){
        assert(data.length == 2 + 1);

        int i = 2;
        voltage = (double)data[i++] * MAX_VOLTAGE / 255;
    }

    public double getVoltage(){
        return voltage;
    }


    public byte[] getBytes() {
        throw new UnsupportedOperationException();
    }
}
