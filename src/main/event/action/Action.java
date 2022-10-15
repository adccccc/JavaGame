package main.event.action;

import main.entity.*;

import java.util.List;

// 游戏物体的执行动作
public abstract class Action {

    // 后续动作列表
    public List<Action> followList;
    // 动作绑定的对象
    GameObject gameObject;
    // 动作的触发条件
    Trigger trigger;
    // 动作是否被触发
    boolean triggered = false;

    public Action(GameObject gameObject, Trigger trigger, List<Action> followList) {

        this.gameObject = gameObject;
        this.trigger = trigger == null ? Trigger.IMMEDIATE : trigger; // 默认立即触发
        this.followList = followList;
    }

    // 如果还没触发, 则检查触发器
    public boolean checkTrigger() { return triggered = triggered || trigger.isTriggered(gameObject); }

    // 动作实际执行内容
    public abstract void execute(GameObject gameObject);

    // 动作是否完成
    public abstract boolean finished();

    // 动作触发条件的事件接口
    public interface Trigger {

        boolean isTriggered(GameObject gameObject);

        // 默认触发器：立即
        Trigger IMMEDIATE = (gameObject -> true);
    }
}
