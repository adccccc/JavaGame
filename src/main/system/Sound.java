package main.system;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.*;
import java.util.Objects;

public class Sound {

    // 动作音效
    public Clip jump, hurt, dead, nextLevel, music;
    // 背景音乐
    public Sequencer titleBgm, gameBgm, bossBgm, currentBgm;
    // 静音
    boolean muted;
    // 背景音乐播放位置记录
    long bgmPosition = 0;

    public Sound() {

        try {
            // 加载音效
            // 这里的InputStream要包装成BufferedInputStream, 才能提供给AudioSystem调用, 不然可能会IOException
            (jump = AudioSystem.getClip()).open(AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream("/sounds/t_1.wav"))));
            (hurt = AudioSystem.getClip()).open(AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream("/sounds/rp.wav"))));
            (dead = AudioSystem.getClip()).open(AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream("/sounds/j.wav"))));
            (music = AudioSystem.getClip()).open(AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream("/sounds/mck.wav"))));
            (nextLevel = AudioSystem.getClip()).open(AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream("/sounds/lblhnkg.wav"))));

            // 加载MIDI背景音乐
            titleBgm = loadMidi("jntmRemix");
            gameBgm = loadMidi("jntm");
            bossBgm = loadMidi("jntmRemix");
            currentBgm = titleBgm;
        } catch (Exception e) { e.printStackTrace();} // do nothing
    }

    private Sequencer loadMidi(String bgmName) throws MidiUnavailableException, InvalidMidiDataException, IOException {

        Sequencer sequencer = MidiSystem.getSequencer();
        sequencer.open();
        sequencer.setSequence(new BufferedInputStream(Objects.requireNonNull(getClass().getResourceAsStream("/sounds/" + bgmName + ".mid"))));
        sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        return sequencer;
    }

    public void playEffect(Clip clip) {

        if (muted) return;

        clip.setFramePosition(0);
        clip.start();
    }

    public void playBgm()  {if (!muted) currentBgm.start();}

    public void changeBgm(Sequencer sequencer) {

        if (sequencer == currentBgm) return;

        currentBgm.stop();
        bgmPosition = 0;
        currentBgm = sequencer;
        playBgm();
    }

    public void changeMute() {if (muted = !muted) pauseBgm(); else resumeBgm();}

    public void pauseBgm() { bgmPosition = currentBgm.getMicrosecondPosition(); currentBgm.stop();}

    public void resumeBgm() {

        currentBgm.setMicrosecondPosition(bgmPosition);
        playEffect(music); // 切换的音效
        playBgm();
    }
}