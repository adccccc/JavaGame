package main;

import main.system.GamePanel;

import javax.swing.*;

public class Main {

    public static void main(String[] args) throws Exception {

        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("小黑子的大冒险");

        window.add(GamePanel.instance);
        window.pack();  // show window
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        GamePanel.instance.setupGame();
    }
}
