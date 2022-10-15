package main.entity;

import main.system.collision.CollisionChecker;
import main.Constant;
import main.system.GamePanel;
import main.system.KeyHandler;
import main.tool.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Player extends Entity {

    GamePanel gp = GamePanel.instance;
    KeyHandler keyHandler;
    int hp = 1; // 生命值
    int jumpCount = 0; // 已经进行了几段跳
    double jump1Speed = 8.5, jump2Speed = 7.0;
    double jumpReleaseCoefficient = 0.45; // 跳跃键释放后的上升速度系数
    boolean canJump = false; // 跳跃键未释放时的标记，此时不能重复跳跃
    public Rectangle solidRect; // 角色的实际碰撞区域
    public String direction; // 角色朝向
    public boolean landed = false; // 踩在地面上
    public boolean leftCollision, rightCollision, topCollision, bottomCollision, anyCollision = false; // 角色各方向是否产生碰撞
    public boolean collisionOn = false; // 是否产生碰撞
    public int retryNum = 0;

    public Player(KeyHandler keyH) {

        this.keyHandler = keyH;
        resetProperties();
        getPlayerImage();
    }

    public void resetProperties() {

        x = gp.playerInitX;
        y = gp.playerInitY;
        hSpeed = 3;
        vSpeed = 0;
        hp = (gp.MAX_DIFFICULTY - gp.difficulty + 1) * 2 - 1; // 初始生命值与难度的关联公式
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

        if (keyHandler.leftPressed) direction = "left";
        if (keyHandler.rightPressed) direction = "right";
        if (keyHandler.jumpPressed && jumpCount < 2 && canJump) {
            vSpeed = ++jumpCount == 1 ? -jump1Speed : -jump2Speed; // 一段跳和2段跳的速度不一致
            landed = false; // 标记浮空
            canJump = false;
            if (jumpCount == 1) gp.sound.playEffect(gp.sound.jump);
        }
        if (keyHandler.jumpReleased) {
            canJump = true; // 可以再次跳跃了
            keyHandler.jumpReleased = false;
        }

        collisionOn = false;
        CollisionChecker.checkTile(this, this.gp.tileManager);
        if (!collisionOn) {
            if (keyHandler.leftPressed) x -= hSpeed;
            if (keyHandler.rightPressed) x += hSpeed;
        }

        if (landed) {
            jumpCount = 0; // 落地重置跳跃次数
            canJump = !keyHandler.jumpPressed; // 落地后要松开跳跃键才能重置跳跃
        }

        if (vSpeed < 0 && !keyHandler.jumpPressed) // 释放跳跃键后，向上时的速度 * 系数
            vSpeed *= jumpReleaseCoefficient;
        vSpeed += Constant.G; // 先减速,再移动
        y += vSpeed;
    }

    public void draw(Graphics2D g2) {

        BufferedImage image = img;
        g2.drawImage(image, (int)x, (int)y,null);
    }

}
