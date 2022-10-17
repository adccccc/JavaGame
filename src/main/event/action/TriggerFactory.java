package main.event.action;

import main.entity.GameObject;

public class TriggerFactory {

    public static Action.Trigger getTrigger(GameObject gameObject, String triggerName, String triggerParam) {

        return Action.Trigger.IMMEDIATE;
    }

}
