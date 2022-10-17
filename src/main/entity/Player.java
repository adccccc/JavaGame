package main.entity;

import main.system.*;
import main.system.collision.CollisionChecker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Player extends Entity {

    public GamePanel gp;
    KeyHandler keyHandler;
    public int hp = 1; // 生命值
    public int jumpCount = 0; // 已经进行了几段跳

    boolean invincible = false; // 是否受伤后无敌状态
    int invincibleFrameCount = 0; // 无敌帧计数
    final int MAX_INVINCIBLE_FRAME = 50; // 无敌帧总长度
    public DynamicImage standImg, runImg, jumpImg, downImg, invincibleImg; // 角色动态图

    double jump1Speed = 8.5, jump2Speed = 7.0; // 一段大跳,二段小跳
    double jumpReleaseCoefficient = 0.45; // 跳跃键释放后的上升速度系数
    boolean canJump = false; // 跳跃键未释放时的标记，此时不能重复跳跃
    public Rectangle solidRect; // 角色的实际碰撞区域
    public boolean landed = false, onPlatform = false; // 踩在地面上与平台上
    public boolean leftCollisionOn, rightCollisionOn = false; // 是否产生碰撞
    public double platformXDisplacement, platformYDisplacement = 0.0; // 跟随平板的位移距离
    public int retryNum = 0; // 失败总次数

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
        solidRect = new Rectangle(8, 10, 16, 21); // 人物碰撞区域
    }

    public void loadPlayerImage() {

        standImg = setup(10, "stand01.png", "stand02.png");
        runImg = setup(6, "stand02.png", "run03.png");
        jumpImg = setup(6, "jump1.png");
        downImg = setup(5, "down1.png", "down2.png");
        invincibleImg = setup(4,"hurt1.png", "hurt2.png");

        img = standImg;
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
            landed = onPlatform = false; // 标记浮空
            canJump = false;
            if (jumpCount == 1) gp.sound.playEffect(gp.sound.jump);
        }
        if (keyHandler.jumpReleased) {
            canJump = true; // 可以再次跳跃了
            keyHandler.jumpReleased = false;
        }

        leftCollisionOn = rightCollisionOn = false;
        try { // 避免地图外碰撞时的数组越界错误
            CollisionChecker.checkTile(this, this.gp.tileManager);
        } catch (Exception ignored) {}

        if (!leftCollisionOn) { // 左边能走
            if (keyHandler.leftPressed) x -= hSpeed;
            if (platformXDisplacement < 0) x += platformXDisplacement; // 跟板移动
        }
        if (!rightCollisionOn) { // 右边能走
            if (keyHandler.rightPressed) x += hSpeed;
            if (platformXDisplacement > 0) x += platformXDisplacement; // 跟板移动
        }
        platformXDisplacement = 0; // 重置平板移动距离

        if (landed || onPlatform) {
            jumpCount = 0; // 落地重置跳跃次数
            canJump = !keyHandler.jumpPressed; // 落地后要松开跳跃键才能重置跳跃
        }

        if (vSpeed < 0 && !keyHandler.jumpPressed) // 释放跳跃键后减速：向上时的速度需要 * 系数
            vSpeed *= jumpReleaseCoefficient;
        vSpeed += Constant.G; // 先减速,再移动
        y += vSpeed + platformYDisplacement; // 随板移动
        platformYDisplacement = 0; // 重置

        if (invincible && ++invincibleFrameCount >= MAX_INVINCIBLE_FRAME) { // 受伤后的无敌时间
            invincible = false;
            invincibleFrameCount = 0;
        }

        if (y > gp.screenHeight) dead(); // 出底线死亡
    }

    // 受伤
    public void gotHurt() {

        if (invincible) return;// 无敌时免疫伤害
        if (--hp > 0) { // 受伤
            invincible = true; // 无敌
            gp.sound.playEffect(gp.sound.hurt);
        } else dead(); // 死亡
    }

    // 死亡
    public void dead() {

        retryNum++;
        gp.gameState = gp.FAILED_STATE;
        gp.sound.playEffect(gp.sound.dead);
    }

    @Override
    public void draw(Graphics2D g2) {

        // 根据人物状态确定动图
        if (landed || onPlatform)
            img = (keyHandler.leftPressed || keyHandler.rightPressed) ? runImg : standImg;
        else
            img = vSpeed < 0 ? jumpImg : downImg;

        super.draw(g2);
        if (invincible) // 加一层受伤特效
            g2.drawImage(invincibleImg.getImg(), (int)x, (int)y, width, height, null );
    }
}
