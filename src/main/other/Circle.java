package main.other;

import java.awt.*;

public class Circle {

    Point center; // 圆心坐标(相对坐标)
    int radius; // 半径

    public Circle(Point center, int radius) {
        this.center = center;
        this.radius = radius;
    }
}