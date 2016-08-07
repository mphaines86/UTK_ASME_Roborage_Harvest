package messaging;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael Haines and Tanner Hobson on 1/20/2015.
 */
public class MotorMessage implements IMessage {

    public enum Motor {
        LEFT_FRONT_DRIVE_MOTOR(0),
        RIGHT_FRONT_DRIVE_MOTOR(1),
        LIFTER_MOTOR(2),
        HOPPER_MOTOR(3),
        COMBINE_MOTOR(4),
        SERVO_MOTOR(5);

        private static Map<Integer, Motor> map = new HashMap<Integer, Motor>();

        static {
            for (Motor m : Motor.values()) {
                map.put((int)m.getValue(), m);
            }
        }

        private final byte id;
        Motor(int id) {this.id =(byte)id;}
        public static Motor fromByte(byte id) { return map.get((int)id); }
        public byte getValue() {return id;}
    }

    Motor motorId;
    int motorPower;
    public static final double MAX_POWER = 200;

    public MotorMessage(Motor motorId, double motorPower){

        this.motorId = motorId;
        this.motorPower = (int)(motorPower * Math.floor(MAX_POWER/2) + Math.floor(MAX_POWER/2));

    }

    public MotorMessage(byte[] data){
        assert data.length == 4;

        int i = 2;
        this.motorId = Motor.fromByte(data[++i]);
        this.motorPower = (int)data[++i];
    }

    public byte[] getBytes(){
        byte[] ret = new byte[]{
               0, 'm',this.motorId.getValue(),(byte)this.motorPower,
        };
        ret[0] = (byte)ret.length;
        return ret;
    }
}
