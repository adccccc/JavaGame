package main;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

public class Sound {

    Clip clip;
    public URL jump, dead;

    public Sound() {

        jump = getClass().getResource("/sounds/t_1.wav");
        dead = getClass().getResource("/sounds/ngm_2.wav");
    }

    public void setFile(URL url) {

        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) { }
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

    public void playBgm()  {

        try {
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            InputStream is = new BufferedInputStream(getClass().getResourceAsStream("/sounds/jntm.mid"));
            sequencer.setSequence(is);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
        } catch (Exception e) {}
    }

}
