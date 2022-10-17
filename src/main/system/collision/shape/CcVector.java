package main.system.collision.shape;

// 向量坐标，用于表示顶点和边
public class CcVector implements Cloneable{

    public double x, y;
    public CcVector(double x, double y) {this.x = x; this.y = y;}

    // ------------------- 一些向量算法 --------------------
    public CcVector perp() {return new CcVector(0+this.y, -this.x);}
    // 坐标正规化
    public CcVector normalize() {
        double sqrtDot = Math.sqrt(dot(this));
        return new CcVector(x / sqrtDot, y / sqrtDot);
    }
    // 点积
    public double dot(CcVector another) {return this.x * another.x + this.y * another.y;}
    public CcVector minus(CcVector another) {return new CcVector(this.x - another.x, this.y - another.y);}
    public CcVector plus(CcVector another) {return new CcVector(this.x + another.x, this.y + another.y);}
}
