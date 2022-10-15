package main;

import main.system.GamePanel;

import javax.swing.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {

        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle(Constant.GAME_TITLE);

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();  // show window

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.setupGame();
        gamePanel.startGameThread();
    }

}
