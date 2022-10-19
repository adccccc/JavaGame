package main.system;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp;
    public boolean leftPressed, leftReleased, rightPressed, rightReleased, jumpPressed, jumpReleased;

    public KeyHandler(GamePanel gp) {this.gp = gp;}

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {

        // 角色控制
        if (gp.gameState == gp.TITLE_STATE) titleStateKeyPressed(e);
        // 开始游戏
        if (gp.gameState == gp.PLAY_STATE) playStateKeyPressed(e, true);
        // 按P暂停
        if (gp.gameState == gp.PAUSE_STATE || gp.gameState == gp.PLAY_STATE) pauseStateKeyPressed(e);
        // 按R重置
        if ((gp.gameState == gp.FAILED_STATE || gp.gameState == gp.PLAY_STATE) && e.getKeyCode() == KeyEvent.VK_R) { gp.gameState = gp.PLAY_STATE; gp.resetLevel(); }
        // 开关音效
        if (e.getKeyCode() == KeyEvent.VK_M) gp.sound.changeMute();
        if (e.getKeyCode() == KeyEvent.VK_B) gp.ui.showPos = !gp.ui.showPos;
    }

    public void keyReleased(KeyEvent e) { playStateKeyPressed(e, false); }

    private void titleStateKeyPressed(KeyEvent e) {

        int code = e.getKeyCode();
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) gp.ui.currentCommand = (++gp.ui.currentCommand > gp.ui.COMMAND_MAX_NUM ? 1 : gp.ui.currentCommand);
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) gp.ui.currentCommand = (--gp.ui.currentCommand < 1 ? gp.ui.COMMAND_MAX_NUM : gp.ui.currentCommand);
        if (code == KeyEvent.VK_ENTER && gp.ui.currentCommand == 1) gp.startGame();
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            if (gp.ui.currentCommand == 2) gp.currentLevel = --gp.currentLevel < 1 ? gp.TOTAL_LEVEL : gp.currentLevel;
            if (gp.ui.currentCommand == 3) gp.difficulty = --gp.difficulty < 1 ? gp.MAX_DIFFICULTY : gp.difficulty;
            if (gp.ui.currentCommand == 4) gp.sound.changeMute();
        } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            if (gp.ui.currentCommand == 2) gp.currentLevel = ++gp.currentLevel > gp.TOTAL_LEVEL ? 1 : gp.currentLevel;
            if (gp.ui.currentCommand == 3) gp.difficulty = ++gp.difficulty > gp.MAX_DIFFICULTY ? 1 : gp.difficulty;
            if (gp.ui.currentCommand == 4) gp.sound.changeMute();
        }
    }

    private void playStateKeyPressed(KeyEvent e, boolean pressed) {

        int code = e.getKeyCode();
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = pressed;
            leftReleased = !pressed;
            gp.player.turnLeft = true;
        } else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = pressed;
            rightReleased = !pressed;
            gp.player.turnLeft = false;
        }

        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_SHIFT) { jumpReleased = ! (jumpPressed = pressed); }
    }

    private void pauseStateKeyPressed(KeyEvent e) {if (e.getKeyCode() == KeyEvent.VK_P) GamePanel.instance.changePause();}
}
