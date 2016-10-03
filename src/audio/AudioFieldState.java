package audio;

/**
 * Created by michael on 9/5/16.
 */
public class AudioFieldState {

    private AudioPlayback startup;


    public AudioFieldState(){

    }

    public void matchCountdown(){
        startup = new AudioPlayback("/home/michael/IdeaProjects/UTK_ASME_field_interface/src/audio/SSM_Narrator.wav");
        startup.playSound();

    }

    public void matchMusic(){
        startup = new AudioPlayback("/home/michael/IdeaProjects/UTK_ASME_field_interface/src/audio/04 - HARDER, BETTER, FASTER, STRONGER.MP3");
        startup.playSound();
    }

    public void endSound(){
        startup = new AudioPlayback("/home/michael/IdeaProjects/UTK_ASME_field_interface/src/audio/nr_name2f.wav");
        startup.playSound();
    }

    public void fadeSound(){
        startup.fadeSound();
    }
    public void killSounds(){
        startup.stopSound();
    }
}
