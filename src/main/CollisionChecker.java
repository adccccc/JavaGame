package main;

import main.entity.GameObject;
import main.entity.Player;
import main.tile.TileManager;

import java.awt.*;

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

        int leftX = player.x + player.solidRect.x;
        int rightX = leftX + player.solidRect.width;
        int topY = player.y + player.solidRect.y;
        int bottomY = topY + player.solidRect.height;

        int entityLeftCol = leftX / Constant.TILE_SIZE;
        int entityRightCol = rightX / Constant.TILE_SIZE;
        int entityTopRow = topY / Constant.TILE_SIZE;
        int entityBottomRow = bottomY / Constant.TILE_SIZE;

        int tileNum1, tileNum2;
        if (Constant.Direction.LEFT.equals(player.direction)) {
            int nextLeftCol = (leftX - player.hSpeed) / Constant.TILE_SIZE;
            tileNum1 = tileManager.mapTileNum[nextLeftCol][entityTopRow];
            tileNum2 = tileManager.mapTileNum[nextLeftCol][entityBottomRow];
            if (tileManager.tile[tileNum1].collision || tileManager.tile[tileNum2].collision)
                player.collisionOn = true;
        } else if (Constant.Direction.RIGHT.equals(player.direction)) {
            int nextRightCol = (rightX + player.hSpeed) / Constant.TILE_SIZE;
            tileNum1 = tileManager.mapTileNum[nextRightCol][entityTopRow];
            tileNum2 = tileManager.mapTileNum[nextRightCol][entityBottomRow];
            if (tileManager.tile[tileNum1].collision || tileManager.tile[tileNum2].collision)
                player.collisionOn = true;
        }

        // TODO: 数组越界, 跳到地图外的情况
        entityTopRow = (topY + player.vSpeed) / Constant.TILE_SIZE;
        tileNum1 = tileManager.mapTileNum[entityLeftCol][entityTopRow];
        tileNum2 = tileManager.mapTileNum[entityRightCol][entityTopRow];
        if (tileManager.tile[tileNum1].collision || tileManager.tile[tileNum2].collision) {
            player.vSpeed = 0; // 往上跳，碰撞
        }
        entityBottomRow = (bottomY + player.vSpeed) / Constant.TILE_SIZE;
        tileNum1 = tileManager.mapTileNum[entityLeftCol][entityBottomRow];
        tileNum2 = tileManager.mapTileNum[entityRightCol][entityBottomRow];
        if (tileManager.tile[tileNum1].collision || tileManager.tile[tileNum2].collision) {
            player.vSpeed = 0; // 往下落，碰撞
            player.landed = true; // 踩在地面上
            player.y = (entityBottomRow - 1) * Constant.TILE_SIZE; // 调整位置，贴住地面
        } else if (tileManager.tile[tileNum1].platform || tileManager.tile[tileNum2].platform) {
            if ((player.vSpeed > 0 || player.landed) && bottomY < entityBottomRow * Constant.TILE_SIZE) { // 仅在下落的时候碰撞
                player.vSpeed = 0;
                player.landed = true;
                player.y = (entityBottomRow - 1) * Constant.TILE_SIZE; // 调整位置，贴住地面
            }
        } else {
            player.landed = false; // 浮空
        }
    }

    public static void checkGameObject(Player player, GameObject gameObject) {


    }

    /**
     * 多边形碰撞检测
     *
     * @param posA 物体A坐标
     * @param areaA   物体A多边形各顶点相对坐标
     * @param posB 物体B坐标
     * @param areaB   物体B多边形各顶点相对坐标
     * @param offsetX A的x坐标偏移，用于运动物体检测
     * @param offsetY A的y坐标偏移，用于运动物体检测
     * @return
     */
    public static boolean collisionCompute(Point posA, java.util.List<Point> areaA, Point posB, java.util.List<Point> areaB, int offsetX, int offsetY) {

        return false;
    }
}
