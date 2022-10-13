package main;

import java.awt.*;

public class UI {

    GamePanel gp;
    Font ariel40;

    public UI (GamePanel gp) {

        this.gp = gp;
        ariel40 = new Font("Arial", Font.PLAIN, 40);
    }

    public void draw(Graphics2D g2) {

        g2.setFont(ariel40);
        g2.setColor(Color.BLACK);
        g2.drawString("Try: " + gp.player.retryNum, 20, 40);
    }

}
