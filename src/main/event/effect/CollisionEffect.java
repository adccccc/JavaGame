package main.event.effect;

import main.entity.GameObject;
import main.entity.Player;
import main.system.collision.shape.CcVector;

// 物体碰撞到人物时产生的效果
public interface CollisionEffect {

    void onCollision(GameObject gameObject, Player player);

    // 效果：无事发生
    CollisionEffect NOTHING = (((gameObject, player) -> {}));

    // 效果：伤害玩家, 并显形
    CollisionEffect HURT_PLAYER = ((gameObject, player) -> {
        player.gotHurt();
        gameObject.visible = true;
    });

    // 效果：移除物体
    CollisionEffect OBJECT_REMOVED = ((gameObject, player) -> gameObject.removed = true);

    // 效果：物体隐形
    CollisionEffect OBJECT_INVISIBLE = ((gameObject, player) -> gameObject.visible = false);

    // 效果：通关
    CollisionEffect NEXT_LEVEL = ((gameObject, player) -> player.gp.nextLevel());

    // 效果：存档
    CollisionEffect SAVE_POINT = (((gameObject, player) -> {
        player.gp.playerInitPos = new CcVector(gameObject.pos); // 本关重生位置改为当前物体坐标
        gameObject.removed = true; // 移除
    }));

    // 效果：平台
    CollisionEffect PLATFORM = (((gameObject, player) -> {

        player.jumpCount = 1; // 碰跳板给小跳
        // 这个判断条件的-1为了抵消+1
        if (player.speed.y >= 0 && (player.pos.y + player.box.y - player.speed.y - 1 <= gameObject.pos.y)) { // 仅在下落的时候碰撞
            player.speed.y = 0;
            player.onPlatform = true;
            player.jumpCount = 0; // 踩板重置跳跃次数
            player.pos.y = gameObject.pos.y - player.box.y + 1; // 调整位置，贴住平台（+1是为了固定碰撞位置，因为不加1碰不到，人物要往下掉，会闪）
            player.platformSpeed = new CcVector(gameObject.speed); // 保存平板位移
        }
    }));
}
