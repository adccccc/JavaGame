package main.system.collision;

import main.system.Constant;
import main.entity.*;

import main.system.collision.shape.*;
import main.entity.TileManager;

import java.util.Arrays;

/**
 * 碰撞检测器
 */
public class CollisionChecker {

    /**
     * 检测人物与瓦片产生碰撞
     * 只取人物周围的4个瓦片来判断
     * @param player 人物
     * @param tileManager 瓦片管理器
     */
    public static void checkTile(Player player, TileManager tileManager) {


        int leftX = (int)player.pos.x + player.solidRect.x;
        int rightX = leftX + player.solidRect.width;
        int topY = (int)player.pos.y + player.solidRect.y;
        int bottomY = topY + player.solidRect.height;

        int entityLeftCol = leftX / Constant.TILE_SIZE;
        int entityRightCol = rightX / Constant.TILE_SIZE;
        int entityTopRow = topY / Constant.TILE_SIZE;
        int entityBottomRow = bottomY / Constant.TILE_SIZE;

        int tileNum1, tileNum2;
        // 左边
        int nextLeftCol = (leftX - (int)player.speed.x - 1) / Constant.TILE_SIZE;
        tileNum1 = tileManager.mapTileNum[nextLeftCol][entityTopRow];
        tileNum2 = tileManager.mapTileNum[nextLeftCol][entityBottomRow];
        if (tileManager.tile[tileNum1].collision || tileManager.tile[tileNum2].collision)
            player.leftCollisionOn = true;

        // 右边
        int nextRightCol = (rightX + (int)player.speed.x + 1) / Constant.TILE_SIZE;
        tileNum1 = tileManager.mapTileNum[nextRightCol][entityTopRow];
        tileNum2 = tileManager.mapTileNum[nextRightCol][entityBottomRow];
        if (tileManager.tile[tileNum1].collision || tileManager.tile[tileNum2].collision)
            player.rightCollisionOn = true;

        // 上边
        entityTopRow = (topY + (int)player.speed.y - 1) / Constant.TILE_SIZE;
        tileNum1 = tileManager.mapTileNum[entityLeftCol][entityTopRow];
        tileNum2 = tileManager.mapTileNum[entityRightCol][entityTopRow];
        if (tileManager.tile[tileNum1].collision || tileManager.tile[tileNum2].collision)
            player.speed.y = player.platformSpeed.y = 0; // 往上跳，碰撞 (并且落板)

        // 下边
        entityBottomRow = (bottomY + (int)player.speed.y + 1) / Constant.TILE_SIZE;
        tileNum1 = tileManager.mapTileNum[entityLeftCol][entityBottomRow];
        tileNum2 = tileManager.mapTileNum[entityRightCol][entityBottomRow];

        if (tileManager.tile[tileNum1].collision || tileManager.tile[tileNum2].collision) {
            player.speed.y = 0; // 往下落，碰撞
            player.landed = true; // 踩在地面上
            player.pos.y = (entityBottomRow - 1) * Constant.TILE_SIZE; // 调整位置，贴住地面
        } else {
            player.landed = false;
        }
    }

    // 游戏物体碰撞检测
    public static void checkGameObject(Player player, GameObject gameObject) {

        // 物体的矩形边界转换成多边形
        CcPolygon absoluteSolidArea = new CcPolygon(player.solidRect.width, player.solidRect.height).plusOffset(player.pos.plus(new CcVector(player.solidRect.x, player.solidRect.y)));

        if (gameObject.shape == 0 && polygonsCollide(absoluteSolidArea, new CcPolygon(gameObject.getCollisionPolyForNull(), gameObject.getScreenStartPos())))
            gameObject.onCollision(player);
        if (gameObject.shape == 1 && circleCollide(absoluteSolidArea, new CcCircle(gameObject.pos.x + gameObject.box.x / 2.0, gameObject.pos.y + gameObject.box.y / 2.0, gameObject.collisionRadius)))
            gameObject.onCollision(player);
    }

    /**
     * 圆形碰撞检测
     * 没有代码空间了，使用8边形模拟圆形，复用代码
     * TODO 精确算法待实现
     */
    private static boolean circleCollide(CcPolygon a, CcCircle b) { return polygonsCollide(a, approximateCircle(b)); }

    /**
     * 将圆形近似为正八边形
     * 太丑陋了。。。。
     */
    private static CcPolygon approximateCircle(CcCircle c) {

        double x = c.x, y = c.y, r = c.radius, p = Math.sqrt(c.radius); // 投影
        return new CcPolygon(new CcPolygon(new CcVector(0, r), new CcVector(p, p), new CcVector(r, 0), new CcVector(p, -p), new CcVector(0, -r), new CcVector(-p, -p), new CcVector(-r, 0), new CcVector(-p, p)), new CcVector(x, y));
    }

    /**
     * 多边形碰撞判定
     * 先粗略检测，再精细检测
     */
    private static boolean polygonsCollide(CcPolygon a, CcPolygon b) { return boardCollide(a.outerBoundingBox(), b.outerBoundingBox()) && narrowCollide(a, b); }

    /**
     * 多边形碰撞判定: 粗略检测
     * 计算外包围矩形是否相交
     */
    private static boolean boardCollide(double[][] a, double[][] b) { return a[0][0] <= b[1][0] && b[0][0] <= a[1][0] && a[0][1] <= b[2][1] && b[0][1] <= a[2][1]; }

    /**
     * 多边形碰撞判定：精细检测
     * 遍历两个多边形的每一条边，取其法向量作为投影轴，检查投影轴是否能分割两个多边形
     * 只要有任意一个投影轴能将其分隔开，则这两个多边形没有碰撞
     */
    private static boolean narrowCollide(CcPolygon a, CcPolygon b) {

        CcVector[] aPoints = a.basePoints;
        CcVector[] bPoints = b.basePoints;
        for (CcVector n : a.normals) if (isSeparatingAxis(aPoints, bPoints, n)) return false;
        for (CcVector n : b.normals) if (isSeparatingAxis(aPoints, bPoints, n)) return false;

        return true;
    }

    /**
     * 判定当前投影轴是否能分割这两个多边形
     * 将多边形的所有顶点投影到轴上，得到两个投影区间；
     * 如果投影区间不相交，则此轴能将这两个多边形分割
     *
     * @param aPoints A的顶点集
     * @param bPoints B的顶点集
     * @param axis 投影轴
     * @return 是否分离
     */
    private static boolean isSeparatingAxis(CcVector[] aPoints, CcVector[] bPoints, CcVector axis) {

        double[] rangeA = flattenPointsOn(aPoints, axis);
        double[] rangeB = flattenPointsOn(bPoints, axis);
        return rangeA[0] > rangeB[1] || rangeB[0] > rangeA[1];
    }

    /**
     * 计算多边形顶点的投影区间
     * @param points 多边形顶点
     * @param normal 投影轴
     * @return 区间最大值与最小值
     */
    private static double[] flattenPointsOn(CcVector[] points, CcVector normal) {

        double max = Arrays.stream(points).map(point -> point.dot(normal)).max(Double::compare).orElse(Double.MAX_VALUE);
        double min = Arrays.stream(points).map(point -> point.dot(normal)).min(Double::compare).orElse(Double.MIN_VALUE);
        return new double[] {min, max};
    }
}
