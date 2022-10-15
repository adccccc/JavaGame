package main.entity;

import java.awt.image.BufferedImage;

public class Tile {

    public BufferedImage image;
    public boolean collision = false;
    public boolean platform = false; // 平台效果，仅上边缘会与人物底部产生碰撞
}
