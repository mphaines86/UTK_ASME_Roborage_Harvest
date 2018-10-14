package audio; /**
 * Created by michael on 9/5/16.
 */

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AudioPlayback{

    private Media mediaData;
    private MediaPlayer mediaPlayer;

    public AudioPlayback(String filename){
        mediaData = new Media(new File(filename).toURI().toString());
        mediaPlayer = new MediaPlayer(mediaData);

    }

    public void playSound() {
        mediaPlayer.play();

    }

    public void stopSound(){
        mediaPlayer.stop();
    }

    public void fadeOutSound(){

        double volume = 1;

        final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            double volume = 1;

            @Override
            public void run() {
                if (volume <= 0){
                    stopSound();
                    timer.cancel();
                    timer.purge();
                }
                else{
                    //System.out.println(volume-=.0025);
                    mediaPlayer.setVolume(volume-=.0025);
                }
            }
        }, 0, 10);

    }

    public void fadeInSound(){

        double volume = 1;

        final Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            double volume = 1;

            @Override
            public void run() {
                if (volume <= 0){
                    stopSound();
                    timer.cancel();
                    timer.purge();
                }
                else{
                    //System.out.println(volume-=.0025);
                    mediaPlayer.setVolume(volume+=.0025);
                }
            }
        }, 0, 10);

    }

}
