package main.event.effect;

import main.entity.*;
import main.system.GamePanel;

import java.util.List;

// 游戏物体的执行动作
public abstract class AbstractAction {

    // 动作绑定的对象
    private GameObject gameObject;
    // 动作的触发条件
    private IActionTrigger trigger;
    // 后续动作列表
    private List<AbstractAction> next;
    // 动作是否被触发
    private boolean triggered;

    public AbstractAction(GameObject gameObject, IActionTrigger trigger, List<AbstractAction> next) {
        this.gameObject = gameObject;
        this.trigger = trigger;
        this.next = next;
        this.triggered = false;
    }

    public boolean checkTrigger() { return triggered = triggered || trigger.isTriggered(gameObject); }

    // 动作实际执行内容
    protected abstract void execute(GameObject gameObject, Player player);

    // 动作是否完成
    public abstract boolean finished();

}
