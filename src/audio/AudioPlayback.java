package audio; /**
 * Created by michael on 9/5/16.
 */

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

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

    }

    public void playSound() {
        mediaPlayer = new MediaPlayer(mediaData);
        mediaPlayer.play();

    }

    public void stopSound(){
        mediaPlayer.stop();
    }

    public void seekSound(double time){
        mediaPlayer.seek(new Duration(time));
    }
}
