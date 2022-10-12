package entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyHandler;

    public Player(GamePanel gp, KeyHandler keyH) {

        this.gp = gp;
        this.keyHandler = keyH;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {

        x = 100;
        y = 100;
        speed = 4;
        solidArea = new Rectangle();
    }

    public void getPlayerImage() {

        try {
            img = ImageIO.read(getClass().getResourceAsStream("/player/1.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {

        if (keyHandler.upPressed) {
            y -= speed;
        }
        if (keyHandler.downPressed) {
            y += speed;
        }
        if (keyHandler.leftPressed) {
            x -= speed;
        }
        if (keyHandler.rightPressed) {
            x += speed;
        }

        collisionOn = false;
        gp.collisionChecker.checkTile(this);

    }

    public void draw(Graphics2D g2) {

        BufferedImage image = img;
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }

}
