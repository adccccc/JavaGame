package main.event.action;

import main.entity.GameObject;
import main.system.GamePanel;

import java.awt.*;

public class TriggerFactory {

    /**
     * 动作触发器的静态工厂方法
     */
    public static Action.Trigger getTrigger(GameObject gameObject, String triggerName, String triggerParam) {

        if ("box".equals(triggerName)) return new BoxTrigger(triggerParam);

        return Action.Trigger.IMMEDIATE;
    }

    // ----------------------------- Trigger实现类 -----------------------------------

    /**
     * 检测人物是否出现在矩形内，用于触发陷阱
     * 只检测人物中心点（简化）
     */
    public static class BoxTrigger implements Action.Trigger {

        private Rectangle box;
        public BoxTrigger(String triggerParam) {
            String[] boxArr = triggerParam.split(",");
            box = new Rectangle(Integer.parseInt(boxArr[0]), Integer.parseInt(boxArr[1]), Integer.parseInt(boxArr[2]), Integer.parseInt(boxArr[3]));
        }

        public boolean isTriggered(GameObject gameObject) { return box.contains(GamePanel.instance.player.x + 16, GamePanel.instance.player.y + 16); }
    }
}
