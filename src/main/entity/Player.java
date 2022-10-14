package main.entity;

import main.CollisionChecker;
import main.Constant;
import main.GamePanel;
import main.KeyHandler;
import main.tool.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyHandler;
    int jumpSize = 0;
    int jumpPressedFrame = 0;
    int maxJumpFrame = 10;
    public Rectangle solidRect; // 角色的实际碰撞区域
    public String direction; // 角色朝向
    public boolean landed = false; // 踩在地面上
    public boolean collisionOn = false; // 是否产生碰撞
    public int retryNum = 0;

    public Player(GamePanel gp, KeyHandler keyH) {

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
        solidRect = new Rectangle(6, 6, 20, 25);
    }

    public void getPlayerImage() {

        img = setup("1");
    }

    public BufferedImage setup(String imageName) {

        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/" + imageName + ".png")));
            image = UtilityTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (Exception e) {}
        return image;
    }

    public void update() {

        if (keyHandler.leftPressed)
            direction = "left";
        if (keyHandler.rightPressed)
            direction = "right";
        if (keyHandler.jumpPressed && jumpSize < 2 && ++jumpPressedFrame <= maxJumpFrame) {
            vSpeed = -10;
            landed = false; // 标记浮空
            if (jumpPressedFrame == 1) {// 只在起跳第一帧播放音效
                gp.sound.setFile(gp.sound.jump);
                gp.sound.play();
            }
        }
        if (keyHandler.jumpReleased) {
            jumpPressedFrame = 0;
            jumpSize++;
            keyHandler.jumpReleased = false;
        }

        collisionOn = false;
        CollisionChecker.checkTile(this, this.gp.tileManager);
        if (!collisionOn) {
            if (keyHandler.leftPressed) {
                x -= hSpeed;
            }
            if (keyHandler.rightPressed) {
                x += hSpeed;
            }
        }

        if (landed)
            jumpSize = 0; // 落地重置跳跃次数

        y += vSpeed;
        vSpeed += Constant.G;
    }

    public void draw(Graphics2D g2) {

        BufferedImage image = img;
        g2.drawImage(image, x, y,null);
    }

}
