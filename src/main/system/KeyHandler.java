package main.system;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp = GamePanel.instance;
    public boolean leftPressed, leftReleased, rightPressed, rightReleased;
    public boolean jumpPressed, jumpReleased;
    public boolean firePressed;

    public KeyHandler() {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {

        if (gp.gameState == gp.TITLE_STATE) titleStateKeyPressed(e);
        if (gp.gameState == gp.PLAY_STATE) playStateKeyPressed(e, true);
        if (gp.gameState == gp.PAUSE_STATE || gp.gameState == gp.PLAY_STATE) pauseStateKeyPressed(e);
        if (e.getKeyCode() == KeyEvent.VK_M) gp.sound.changeMute(); // 音量开关
    }


    @Override
    public void keyReleased(KeyEvent e) {

        playStateKeyPressed(e, false);
    }

    private void titleStateKeyPressed(KeyEvent e) {

        int code = e.getKeyCode();
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) gp.ui.currentCommand = ++gp.ui.currentCommand > gp.ui.COMMAND_MAX_NUM ? 1 : gp.ui.currentCommand;
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) gp.ui.currentCommand = --gp.ui.currentCommand < 1 ? gp.ui.COMMAND_MAX_NUM : gp.ui.currentCommand;
        if (code == KeyEvent.VK_ENTER && gp.ui.currentCommand == 1) gp.startGame();
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            if (gp.ui.currentCommand == 2) gp.currentLevel = --gp.currentLevel < 1 ? gp.TOTAL_LEVEL : gp.currentLevel;
            if (gp.ui.currentCommand == 3) gp.difficulty = --gp.difficulty < 1 ? gp.MAX_DIFFICULTY : gp.difficulty;
            if (gp.ui.currentCommand == 4) gp.sound.muted = !gp.sound.muted;
        } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            if (gp.ui.currentCommand == 2) gp.currentLevel = ++gp.currentLevel > gp.TOTAL_LEVEL ? 1 : gp.currentLevel;
            if (gp.ui.currentCommand == 3) gp.difficulty = ++gp.difficulty > gp.MAX_DIFFICULTY ? 1 : gp.difficulty;
            if (gp.ui.currentCommand == 4) gp.sound.muted = !gp.sound.muted;
        }
    }

    private void playStateKeyPressed(KeyEvent e, boolean pressed) {

        int code = e.getKeyCode();
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) { leftPressed = pressed; leftReleased = !pressed; }
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) { rightPressed = pressed; rightReleased = !pressed; }
        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_SHIFT) { jumpPressed = pressed; jumpReleased = !pressed; }
        if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_X) { firePressed = pressed; }
    }

    private void pauseStateKeyPressed(KeyEvent e) {if (e.getKeyCode() == KeyEvent.VK_P) GamePanel.instance.changePause();}
}
