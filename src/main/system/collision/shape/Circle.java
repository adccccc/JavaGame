package main.system.collision.shape;

public class Circle {

    public double x, y; // 圆心坐标(绝对)
    public double radius; // 半径

    public Circle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }
}