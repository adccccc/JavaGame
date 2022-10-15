package main.event.effect;

import main.entity.GameObject;
import main.entity.Player;

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
}
