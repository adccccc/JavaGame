package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {

    public int x, y;
    public int hSpeed;
    public int vSpeed; // vertical speed
    public BufferedImage img;
    public String direction;

    public Rectangle solidArea;
    public boolean collisionOn = false;
    public boolean landed = false; // 踩在地面上
}
