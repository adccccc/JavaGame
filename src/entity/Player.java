package entity;

import main.Constant;
import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyHandler;
    int jumpSize = 0;
    int jumpPressedFrame = 0;
    int maxJumpFrame = 10;
    public int retryNum = 0;

    public Player(GamePanel gp, KeyHandler keyH) throws IOException {

        this.gp = gp;
        this.keyHandler = keyH;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {

        x = 100;
        y = 100;
        hSpeed = 4;
        vSpeed = 0;
        solidArea = new Rectangle(8, 8, 16, 16);
    }

    public void getPlayerImage() throws IOException {

        img = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/1.png")));
    }

    public void update() {

        if (keyHandler.leftPressed)
            direction = "left";
        if (keyHandler.rightPressed)
            direction = "right";
        if (keyHandler.jumpPressed && jumpSize < 2 && ++jumpPressedFrame <= maxJumpFrame) {
            vSpeed = -10;
            gp.sound.setFile(gp.sound.jump);
            gp.sound.play();

        }
        if (keyHandler.jumpReleased) {
            jumpPressedFrame = 0;
            jumpSize++;
            keyHandler.jumpReleased = false;
        }

        collisionOn = false;
        gp.collisionChecker.checkTile(this);
        if (!collisionOn) {
            if (keyHandler.leftPressed) {
                x -= hSpeed;
            }
            if (keyHandler.rightPressed) {
                x += hSpeed;
            }
        }

        if (landed)
            jumpSize = 0;

        y += vSpeed;
        vSpeed += Constant.G;
    }

    public void draw(Graphics2D g2) {

        BufferedImage image = img;
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }

}
