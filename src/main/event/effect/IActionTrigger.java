package main.event.effect;

import main.entity.GameObject;

// 物体动作的触发器
public interface IActionTrigger {

    boolean isTriggered(GameObject gameObject);

    // 默认触发器：立即
    IActionTrigger IMMEDIATE = (gameObject -> true);
}
