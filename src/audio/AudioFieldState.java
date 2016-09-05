package audio;

/**
 * Created by michael on 9/5/16.
 */
public class AudioFieldState {

    public AudioFieldState(){

    }

    public void matchCountdown(){
        AudioPlayback startup = new AudioPlayback("/home/michael/IdeaProjects/UTK_ASME_field_interface/src/audio/SSM_Narrator.wav");

        startup.playSound();
    }
}
