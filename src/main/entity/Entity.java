package main.entity;

import main.other.Circle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Entity {

    public BufferedImage img; // 贴片图
    public int x, y; // 全局坐标 (左上角为0,0)
    public int hSpeed,vSpeed; // 水平/垂直速度，正向为右下
    public int hAcceleration, vAcceleration; // 水平/垂直加速度，正向为右下
}
