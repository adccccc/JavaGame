package main;

import javax.sound.sampled.*;
import java.net.URL;

public class Sound {

    Clip clip;
    public URL jump, dead;

    public Sound() {

        jump = getClass().getResource("/sounds/jump.wav");
        dead = getClass().getResource("/sounds/dead.wav");
    }

    public void setFile(URL url) {

        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {

        clip.start();
    }

    public void loop() {

        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {

        clip.stop();
    }

}
