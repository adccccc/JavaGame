package main.entity;

import main.system.DynamicImage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity implements Cloneable {

    public DynamicImage img; // 贴片图
    public double x = 0.0, y = 0.0; // 全局坐标 (左上角为0,0)
    public int width, height; // 宽度和高度
    public double hSpeed = 0.0, vSpeed = 0.0; // 水平/垂直速度，正向为右下
    public double hAcceleration = 0.0, vAcceleration = 0.0; // 水平/垂直加速度，正向为右下

    public double scale = 1.0; // 缩放比例
    public double rotate = 0.0; // 旋转角度
    public boolean visible = true; // 是否可见
    public boolean removed = false; // 是否移除 (物体被释放，移出游戏)

    public void draw(Graphics2D g2) {

        BufferedImage image = img.getImg();
        g2.drawImage(image, (int)x, (int)y, width, height, null);
    }

    public void reCalcSpeed() {
        hSpeed += hAcceleration;
        vSpeed += vAcceleration;
    }

    public void reCalcLocation() {
        x += hSpeed;
        y += vSpeed;
    }

}
