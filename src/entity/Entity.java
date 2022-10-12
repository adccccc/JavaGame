package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {
    public int x, y;
    public int speed;
    public BufferedImage img;

    public Rectangle solidArea;
    public boolean collisionOn = false;

}
