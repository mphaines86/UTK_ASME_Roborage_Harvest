package audio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


/**
 * Created by michael on 9/5/16.
 */
public class AudioFieldState {

    private AudioPlayback startup;
    private List<String> trackList = new ArrayList<>();
    private int currentTrackNumber = 0;
    private int numberOfTracks;

    public AudioFieldState(){

        try (Stream<Path> paths = Files.walk(Paths.get(System.getProperty("user.dir") + "/src/audio/music/"))){
            paths.forEach(filePath ->{
                if (Files.isRegularFile(filePath)){
                    trackList.add(filePath.toString());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.shuffle(trackList);
        numberOfTracks = trackList.size();
        //System.out.println("Number of Tracks " + Integer.toString(numberOfTracks));
    }

    public void matchCountdown(){
        startup = new AudioPlayback(System.getProperty("user.dir") + "/src/audio/SSM_Narrator.wav");
        startup.playSound();

    }

    public void matchMusic(){
        startup = new AudioPlayback(trackList.get(currentTrackNumber));
        System.out.println(currentTrackNumber);
        startup.playSound();
        currentTrackNumber++;
        if (currentTrackNumber == numberOfTracks){
            Collections.shuffle(trackList);
            currentTrackNumber = 0;
        }
    }

    public void endSound(){
        startup = new AudioPlayback(System.getProperty("user.dir") + "/src/audio/nr_name2f.wav");
        startup.playSound();
    }

    public void abortSound(){
        startup = new AudioPlayback(System.getProperty("user.dir") + "/src/audio/nr_name25.dsp.wav");
        startup.playSound();
    }

    public void pestilenceSound(){
        startup = new AudioPlayback(System.getProperty("user.dir") + "/src/audio/Deception Horn.mp3");
        startup.playSound();
    }

    public void fadeOutSound(){
        startup.fadeOutSound();
    }

    public void fadeInSound(){
        startup.fadeInSound();
    }
    public void killSounds(){
        startup.stopSound();
    }
}
