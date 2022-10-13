package main;

import java.awt.*;
import java.text.DecimalFormat;

public class UI {

    GamePanel gp;
    Font ariel40;
    public boolean gamePaused = false;
    double playTime;
    DecimalFormat dFormat = new DecimalFormat("#00.00");

    public UI (GamePanel gp) {

        this.gp = gp;
        ariel40 = new Font("宋体", Font.BOLD, 30);
    }

    public void draw(Graphics2D g2) {

        if (gamePaused) {

        } else {
            g2.setFont(ariel40);
            g2.setColor(Color.ORANGE);
            g2.drawString("你干嘛： " + gp.player.retryNum, 20, 40);
            playTime += (double)1/gp.FPS;
            int minutes = (int)playTime / 60;
            g2.drawString("Time: " + minutes + ":" + dFormat.format(playTime - minutes * 60), gp.tileSize * 11, 65);
        }
    }

}
