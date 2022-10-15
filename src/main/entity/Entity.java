package main.entity;

import java.awt.image.BufferedImage;

public class Entity {

    public BufferedImage img; // 贴片图
    public double x, y; // 全局坐标 (左上角为0,0)
    public double width, height; // 宽度和高度
    public double hSpeed,vSpeed; // 水平/垂直速度，正向为右下
    public double hAcceleration, vAcceleration; // 水平/垂直加速度，正向为右下

    public double scale; // 缩放比例
    public boolean visible; // 是否可见
    public boolean removed; // 是否移除 (物体被释放，移出游戏)
}
