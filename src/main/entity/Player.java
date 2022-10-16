package main.entity;

import main.system.DynamicImage;
import main.system.collision.CollisionChecker;
import main.Constant;
import main.system.GamePanel;
import main.system.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Player extends Entity {

    public GamePanel gp;
    KeyHandler keyHandler;
    int hp = 1; // 生命值
    int jumpCount = 0; // 已经进行了几段跳

    boolean invincible = false; // 是否受伤后无敌状态
    int invincibleFrameCount = 0; // 无敌帧计数
    final int MAX_INVINCIBLE_FRAME = 50; // 无敌帧总长度
    public DynamicImage invincibleImg; // 无敌特效

    double jump1Speed = 8.5, jump2Speed = 7.0;
    double jumpReleaseCoefficient = 0.45; // 跳跃键释放后的上升速度系数
    boolean canJump = false; // 跳跃键未释放时的标记，此时不能重复跳跃
    public Rectangle solidRect; // 角色的实际碰撞区域
    public String direction; // 角色朝向
    public boolean landed = false; // 踩在地面上
    public boolean collisionOn = false; // 是否产生碰撞
    public int retryNum = 0;


    public Player(GamePanel gp, KeyHandler keyH) {

        this.gp = gp;
        this.keyHandler = keyH;
        resetProperties();
        loadPlayerImage();
    }

    public void resetProperties() {

        x = gp.playerInitX;
        y = gp.playerInitY;
        width = 32;
        height = 32;
        hSpeed = 3;
        vSpeed = 0;
        hp = (gp.MAX_DIFFICULTY - gp.difficulty + 1) * 2 - 1; // 初始生命值与难度的关联公式
        solidRect = new Rectangle(8, 8, 16, 23);
    }

    public void loadPlayerImage() {

        img = setup(10, "stand.png", "stand2.png");
        invincibleImg = setup(4,"1.png", "1.png");
    }

    public DynamicImage setup(int frame, String... imageNames) {

        BufferedImage[] images = new BufferedImage[imageNames.length];
        try {
            for (int i = 0; i < imageNames.length; i++)
                images[i] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/" + imageNames[i])));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DynamicImage(frame, images);
    }

    public void update() {

        if (keyHandler.leftPressed) turnLeft = true;
        if (keyHandler.rightPressed) turnLeft = false;
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

        if (invincible && ++invincibleFrameCount >= MAX_INVINCIBLE_FRAME) { // 无敌时间
            invincible = false;
            invincibleFrameCount = 0;
        }
    }

    // 受伤
    public void gotHurt() {

        if (invincible) // 无敌时免疫伤害
            return;
        --hp;
        if (hp == 0) { // 死亡
            retryNum++;
            gp.gameState = gp.FAILED_STATE;
            gp.sound.playEffect(gp.sound.dead);
        } else if (hp > 0) { // 受伤
            invincible = true; // 无敌
            gp.sound.playEffect(gp.sound.hurt);
        }
    }

    @Override
    public void draw(Graphics2D g2) {

        super.draw(g2);
        if (invincible) // 加一层无敌特效
            g2.drawImage(invincibleImg.getImg(), (int)x, (int)y, width, height, null );
    }
}
