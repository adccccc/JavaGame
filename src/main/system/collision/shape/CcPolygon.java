package main.system.collision.shape;

import java.util.List;

// tu多边形
public class CcPolygon {

    public CcVector[] basePoints;
    public CcVector[] edges;
    public CcVector[] normals;

    public CcPolygon(CcVector... points) { resetPoints(points); }

    public CcPolygon(double w, double h) { resetPoints(new CcVector(0,0), new CcVector(0, w), new CcVector(w, h), new CcVector(w, 0));}

    public CcPolygon(List<Double> posList) {

        basePoints = new CcVector[posList.size() / 2];
        for (int i = 0; i < basePoints.length; i++)
            basePoints[i] = new CcVector(posList.get(i*2), posList.get(i * 2 + 1));
        reCalculate();
    }

    public CcPolygon(CcPolygon another, CcVector offset) {

        this.basePoints = new CcVector[another.basePoints.length];
        for (int i = 0; i < another.basePoints.length; i++)
            this.basePoints[i] = another.basePoints[i].plus(offset);
        reCalculate();
    }

    public void resetPoints(CcVector... points) {

        this.basePoints = points.clone();
        reCalculate();
    }

    public void reCalculate() {

        this.edges = new CcVector[basePoints.length];
        this.normals = new CcVector[basePoints.length];
        for (int i = 0; i < basePoints.length; i++) {
            CcVector p1 = basePoints[i];
            CcVector p2 = i == basePoints.length - 1 ? basePoints[0] : basePoints[i+1]; // 如果是最后一个顶点，则与第一个顶点连边
            edges[i] = p2.minus(p1);  // 两点相减得到边
            normals[i] = edges[i].perp().normalize(); // 正则化
        }
    }

    // 计算多边形的外包围框坐标
    public double[][] outerBoundingBox() {

        double xMin = Double.MAX_VALUE, xMax = Double.MIN_VALUE, yMin = Double.MAX_VALUE, yMax = Double.MIN_VALUE;
        for (CcVector point : basePoints) {
            xMin = Math.min(point.x, xMin);
            yMin = Math.min(point.y, yMin);
            xMax = Math.max(point.x, xMax);
            yMax = Math.max(point.y, yMax);
        }
        return new double[][] {{xMin, yMin}, {xMax, yMin}, {xMin, yMax}, {xMax, yMax}};
    }
}
