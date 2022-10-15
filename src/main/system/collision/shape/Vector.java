package main.system.collision.shape;

// 向量坐标，用于表示顶点和边
public class Vector implements Cloneable{

    public double x, y;
    public Vector(double x, double y) {this.x = x; this.y = y;}

    // ------- 一些向量算法 ------
    public Vector perp() {return new Vector(0+this.y, -this.x);}
    // 坐标正规化
    public Vector normalize() {
        double sqrtDot = Math.sqrt(dot(this));
        return new Vector(x / sqrtDot, y / sqrtDot);
    }
    // 点积
    public double dot(Vector another) {return this.x * another.x + this.y * another.y;}
    public Vector minus(Vector another) {return new Vector(this.x - another.x, this.y - another.y);}
    public Vector plus(Vector another) {return new Vector(this.x + another.x, this.y + another.y);}
}
