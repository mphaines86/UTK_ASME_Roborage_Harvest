package messaging;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael Haines on 10/16/2016.
 */
public class TeamMessage implements IMessage {

    public enum Teams {
        Red_TEAM(0),
        BLUE_TEAM(1),
        GREEN_TEAM(2),
        YELLOW_TEAM(3);

        private static Map<Integer, Teams> map = new HashMap<Integer, Teams>();

        static {
            for (Teams t : Teams.values()) {
                map.put((int)t.getValue(), t);
            }
        }

        private final byte id;
        Teams(int id) {this.id =(byte)id;}
        public static Teams fromByte(byte id) { return map.get((int)id); }
        public byte getValue() {return id;}
    }

    private Teams teamsId;
    private int teamsActive;
    private int teamsPoints;
    private int teamsRedBalls;
    private int teamsBlueBalls;
    private int teamsGreenBalls;
    private int teamsPurpleBalls;
    private int teamsRacketBalls;
    private int teamsReadWrite;

    public TeamMessage(Teams teamsId,int teamsActive, int teamsPoints, int teamsRedBalls,
            int teamsBlueBalls, int teamsGreenBalls, int teamsPurpleBalls, int teamsRacketBalls, int teamsReadWrite){

        this.teamsId = teamsId;
        this.teamsActive = teamsActive;
        this.teamsPoints = teamsPoints;
        this.teamsRedBalls = teamsRedBalls;
        this.teamsBlueBalls = teamsBlueBalls;
        this.teamsGreenBalls = teamsGreenBalls;
        this.teamsPurpleBalls = teamsPurpleBalls;
        this.teamsRacketBalls = teamsRacketBalls;
        this.teamsReadWrite = teamsReadWrite;
    }

    public TeamMessage(byte[] data){
        assert data.length == 6;

        int i = 1;
        this.teamsId = Teams.fromByte(data[++i]);
        this.teamsActive = ((int)data[++i]) & 0xFF;
        this.teamsPoints = ((((int) data[++i]) & 0xFF) << 8) | ((int)data[++i]) & 0xFF;
    }

    public Teams getTeamsId(){
        return teamsId;
    }

    public int getTeamsActive(){
        return teamsActive;
    }

    public int getTeamsPoints(){
        return teamsPoints;
    }

    public byte[] getBytes(){
        byte[] ret = new byte[]{
                0, 't',this.teamsId.getValue(), (byte) this.teamsActive, (byte) this.teamsPoints,
                (byte) this.teamsRedBalls, (byte) this.teamsBlueBalls, (byte) this.teamsGreenBalls,
                (byte) this.teamsPurpleBalls, (byte) this.teamsRacketBalls, (byte) this.teamsReadWrite
        };
        ret[0] = (byte)ret.length;
        return ret;
    }
}
