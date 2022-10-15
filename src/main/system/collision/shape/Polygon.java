package main.system.collision.shape;

// 多边形
public class Polygon {

    public Vector[] basePoints;
    public Vector[] edges;
    public Vector[] normals;
    public Polygon(Vector... points) { resetPoints(points); }

    public void resetPoints(Vector[] points) {

        this.basePoints = points.clone();
        reCalculate();
    }

    public void reCalculate() {

        this.edges = new Vector[basePoints.length];
        this.normals = new Vector[basePoints.length];
        for (int i = 0; i < basePoints.length; i++) {
            Vector p1 = basePoints[i];
            Vector p2 = i == basePoints.length - 1 ? basePoints[0] : basePoints[i+1]; // 如果是最后一个顶点，则与第一个顶点连边
            edges[i] = p2.minus(p1);  // 两点相减得到边
            normals[i] = edges[i].perp().normalize(); // 正则化
        }
    }

    // 计算多边形的外包围框坐标
    public double[][] outerBoundingBox() {

        double xMin = Double.MAX_VALUE, xMax = Double.MIN_VALUE, yMin = Double.MAX_VALUE, yMax = Double.MIN_VALUE;
        for (Vector point : basePoints) {
            xMin = Math.min(point.x, xMin);
            yMin = Math.min(point.y, yMin);
            xMax = Math.max(point.x, xMax);
            yMax = Math.max(point.y, yMax);
        }
        return new double[][] {{xMin, yMin}, {xMax, yMin}, {xMin, yMax}, {xMax, yMax}};
    }
}
