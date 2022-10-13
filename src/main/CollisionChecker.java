package main;

import entity.Entity;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {

        this.gp = gp;
    }

    public void checkTile(Entity entity) {

        int entityLeftX = entity.x + entity.solidArea.x;
        int entityRightX = entity.x + entity.solidArea.x + entity.solidArea.width;
        int entityTopY = entity.y + entity.solidArea.y;
        int entityBottomY = entity.y + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftX / gp.tileSize;
        int entityRightCol = entityRightX / gp.tileSize;
        int entityTopRow = entityTopY / gp.tileSize;
        int entityBottomRow = entityBottomY / gp.tileSize;

        int tileNum1, tileNum2;
        if ("left".equals(entity.direction)) {
            int nextLeftCol = (entityLeftX - entity.hSpeed) / gp.tileSize;
            tileNum1 = gp.tileManager.mapTileNum[nextLeftCol][entityTopRow];
            tileNum2 = gp.tileManager.mapTileNum[nextLeftCol][entityBottomRow];
            if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision)
                entity.collisionOn = true;
        } else if ("right".equals(entity.direction)) {
            int nextRightCol = (entityRightX + entity.hSpeed) / gp.tileSize;
            tileNum1 = gp.tileManager.mapTileNum[nextRightCol][entityTopRow];
            tileNum2 = gp.tileManager.mapTileNum[nextRightCol][entityBottomRow];
            if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision)
                entity.collisionOn = true;
        }

        entityTopRow = (entityTopY + entity.vSpeed) / gp.tileSize;
        tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityTopRow];
        tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityTopRow];
        if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
            entity.vSpeed = 0; // 往上跳，碰撞
        }
        entityBottomRow = (entityBottomY + entity.vSpeed) / gp.tileSize;
        tileNum1 = gp.tileManager.mapTileNum[entityLeftCol][entityBottomRow];
        tileNum2 = gp.tileManager.mapTileNum[entityRightCol][entityBottomRow];
        if (gp.tileManager.tile[tileNum1].collision || gp.tileManager.tile[tileNum2].collision) {
            entity.vSpeed = 0; // 往下落，碰撞
            entity.landed = true; // 踩在地面上
        } else {
            entity.landed = false; // 浮空
        }
    }

}
