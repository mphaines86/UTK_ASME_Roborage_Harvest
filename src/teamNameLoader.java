

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Haines on 10/14/2018.
 */


public class teamNameLoader {

    List<String> teamName = new ArrayList<>();
    List<String> schoolName = new ArrayList<>();
    List<String> robotName = new ArrayList<>();
    List<String> robotWeight = new ArrayList<>();

    public teamNameLoader(){

        String teamInfo = System.getProperty("user.dir") + "/src/team-info.csv";
        try(BufferedReader br = new BufferedReader(new FileReader(teamInfo))){
            String line = "";
            while ((line = br.readLine()) != null){
                String[] data = line.split(",");
                teamName.add(data[0]);
                schoolName.add(data[1]);
                robotName.add(data[2]);
                robotWeight.add(data[3]);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        };

    }
    public List<String> getTeamName(){
        return teamName;
    }

    public List<String> getSchoolName(){
        return schoolName;
    }

    public List<String> getRobotName(){
        return robotName;
    }

    public List<String> getRobotWeight(){
        return robotWeight;
    }

    public String getTeamName(int index){
        return teamName.get(index);
    }

    public String getSchoolName(int index){
        return schoolName.get(index);
    }

    public String getRobotName(int index){
        return robotName.get(index);
    }

    public String getRobotWeight(int index){
        return robotWeight.get(index);
    }
}
