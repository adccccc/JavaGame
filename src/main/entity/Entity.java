package main.entity;

import main.system.DynamicImage;
import main.system.collision.shape.Circle;
import main.system.collision.shape.Polygon;
import main.system.collision.shape.Vector;
import main.tool.UtilityTool;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {

    public DynamicImage img; // 贴片图
    public double x = 0.0, y = 0.0; // 全局坐标 (左上角为0,0)
    public int width, height; // 宽度和高度
    public double hSpeed = 0.0, vSpeed = 0.0; // 水平/垂直速度，正向为右下
    public double hAcceleration = 0.0, vAcceleration = 0.0; // 水平/垂直加速度，正向为右下

    public double scale = 1.0; // 缩放比例
    public int rotate = 0; // 旋转角度
    public boolean turnLeft = false; // 是否朝向左 (图片只有右，向左要翻转)
    public boolean visible = true; // 是否可见
    public boolean removed = false; // 是否移除 (物体被释放，移出游戏)
    // 物体形状 0-多边形 1-圆形
    public int shape = 0;

    // 凸多边形端点列表，两两相连，围成物体的撞检测区域
    // 端点坐标为物体坐标(左上角)的相对坐标，一般情况均为正数
    public Polygon collisionPoly;
    // 圆形碰撞半径
    public double collisionRadius;

    // ---- 图形处理相关 ------

    public Polygon getCollisionPolyForNull() {return this.collisionPoly != null ? this.collisionPoly : new Polygon(this.width, this.height);}

    public void draw(Graphics2D g2) {

        BufferedImage image = img.getImg();
        // 存在形变时需要处理
        if (turnLeft) image = UtilityTool.horizontalFlipImage(image);
        if (scale != 1.0) image = UtilityTool.scaleImage(image, scale);
        // 旋转功能屏蔽掉
        // if (rotate != 0) image = UtilityTool.rotate(image, rotate);

        Vector actualStart = getScreenStartPos();
        g2.drawImage(image, (int)actualStart.x, (int)actualStart.y, (int)(width * scale), (int)(height * scale), null);
    }

    // 计算缩放后的绘画起点位置 (中心缩放)
    public Vector getScreenStartPos() {return new Vector(x + width / 2.0 * (1 - scale),  y + height / 2.0 * (1 - scale));}

    // ----- 物体运动相关 ------

    public void reCalcSpeed() {
        hSpeed += hAcceleration;
        vSpeed += vAcceleration;
    }

    public void reCalcLocation() {
        x += hSpeed;
        y += vSpeed;
    }

    public void stopMoving() { hSpeed = vSpeed = hAcceleration = vAcceleration = 0; }

}
