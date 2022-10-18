package main.entity;

import main.system.DynamicImage;
import main.system.collision.shape.*;
import main.tool.UtilityTool;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {

    public DynamicImage img; // 贴片图（动态）
    public CcVector pos = new CcVector(0,0); // 全局坐标 (左上角为 0,0)
    public CcVector box = new CcVector(0,0); // 初始像素宽度和高度
    public CcVector speed = new CcVector(0, 0); // 水平和竖直速度(向下为正)
    // public double hAcceleration = 0.0, vAcceleration = 0.0; // 水平/垂直加速度，正向为右下（舍弃掉这个属性）

    public long surviveTime = 0; // 物体存活时间(帧)
    public double scale = 1.0; // 缩放比例
    public int rotate = 0; // 旋转角度
    public boolean turnLeft = false; // 是否朝向左 (图片只有右，向左要翻转)
    public boolean visible = true; // 是否可见
    public boolean removed = false; // 是否移除 (物体被释放，移出游戏)
    // 物体形状 0-多边形 1-圆形
    public int shape = 0;

    // 凸多边形端点列表，两两相连，围成物体的撞检测区域
    // 端点坐标为物体坐标(左上角)的相对坐标，一般情况均为正数
    public CcPolygon collisionPoly;
    // 圆形碰撞半径
    public double collisionRadius;


    // --------------------------- 图形绘制相关 -----------------------------------

    public CcPolygon getCollisionPolyForNull() {return this.collisionPoly != null ? this.collisionPoly : new CcPolygon(this.box.x, this.box.y);}

    public void draw(Graphics2D g2) {

        // 隐形不画
        if (!visible) return;

        BufferedImage image = img.getImg();
        // 存在形变时需要处理
        if (turnLeft) image = UtilityTool.horizontalFlipImage(image);
        if (scale != 1.0) image = UtilityTool.scaleImage(image, scale);
        // 旋转功能屏蔽掉
        // if (rotate != 0) image = UtilityTool.rotate(image, rotate);

        CcVector actualStart = getScreenStartPos();
        g2.drawImage(image, (int)actualStart.x, (int)actualStart.y, (int)(box.x * scale), (int)(box.y * scale), null);
    }

    // 计算缩放后的绘画起点位置 (中心缩放)
    public CcVector getScreenStartPos() { return pos.plus(new CcVector(box.x/2.0*(1 - scale), box.y / 2.0 * (1 - scale)));}

    // -------------------------- 物体运动状态操控 -----------------------------------

    public void reCalcLocation() { pos = pos.plus(speed); }

    public void stopMoving() { speed.x = speed.y = 0; }

}
