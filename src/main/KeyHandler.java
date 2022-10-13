package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean leftPressed, leftReleased, rightPressed, rightReleased;
    public boolean jumpPressed, jumpReleased;
    public boolean firePressed;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {

        keyAction(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {

        keyAction(e, false);
    }

    private void keyAction(KeyEvent e, boolean pressed) {

        int code = e.getKeyCode();
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = pressed;
            leftReleased = !pressed;
        } else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = pressed;
            rightReleased = !pressed;
        } else if (code == KeyEvent.VK_SPACE) {
            jumpPressed = pressed;
            jumpReleased = !pressed;
        } else if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_X)
            firePressed = pressed;
    }
}
