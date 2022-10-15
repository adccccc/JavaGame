package main.system.collision.shape;

// 向量坐标，用于表示顶点和边
public class Vector {

    public double x, y;
    public Vector(double x, double y) {this.x = x; this.y = y;}

    //
    public Vector perp() {return new Vector(0+this.y, -this.x);}
    // 坐标正规化
    public Vector normalize() {
        double sqrtDot = Math.sqrt(dot(this));
        return new Vector(x / sqrtDot, y / sqrtDot);
    }
    // 点积
    public double dot(Vector another) {return this.x * another.x + this.y * another.y;}
    // 相对向量
    public Vector minus(Vector another) {return new Vector(this.x - another.x, this.y - another.y);};
}
