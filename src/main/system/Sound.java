package main.system;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.*;

public class Sound {

    public Clip jump, dead;
    public Sequencer bgm;
    boolean muted;
    long bgmPosition = 0;

    public Sound() throws Exception {

        // 加载音效
        (jump = AudioSystem.getClip()).open(AudioSystem.getAudioInputStream(getClass().getResourceAsStream("/sounds/t_1.wav")));
        (dead = AudioSystem.getClip()).open(AudioSystem.getAudioInputStream(getClass().getResourceAsStream("/sounds/ngm_2.wav")));
        // 加载MIDI背景音乐
        (bgm = MidiSystem.getSequencer()).open();
        InputStream is = new BufferedInputStream(getClass().getResourceAsStream("/sounds/jntm.mid"));
        bgm.setSequence(is);
        bgm.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
    }

    public void playEffect(Clip clip) {if (!muted) clip.setFramePosition(0); clip.start();}

    public void playBgm()  {if (!muted) bgm.start();}

    public void changeMute() {
        if (muted = !muted) pauseBgm(); // 省一行
        else resumeBgm();
    }

    public void pauseBgm() {bgmPosition = bgm.getMicrosecondPosition(); bgm.stop();}
    public void resumeBgm() {bgm.setMicrosecondPosition(bgmPosition); playBgm();}
}