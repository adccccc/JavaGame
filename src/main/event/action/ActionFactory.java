package main.event.action;

import main.entity.GameObject;
import main.system.GamePanel;

public class ActionFactory {

    public static Action getAction(GameObject gameObject, String actionName, String actionParam, Action.Trigger trigger) {

        Action action = null;
        try {
            if ("speedChange".equals(actionName)) action = getSpeedChangeAction(gameObject, actionParam);
            if ("timerMove".equals(actionName)) action = getTimerMoveAction(gameObject, actionParam);
            if ("wander".equals(actionName)) action = getWanderAction(gameObject, actionParam);
            if ("test".equals(actionName)) action = test(gameObject, actionParam);
            if (action != null) {
                action.gameObject = gameObject;
                action.trigger = trigger;
            }
        } catch (Exception e) { // for debug
            e.printStackTrace();
        }
        return action;
    }

    private static Action getSpeedChangeAction(GameObject gameObject, String actionParam) {

        SpeedChangeAction action = new SpeedChangeAction();
        String[] param = actionParam.split(",");
        action.vSpeed = Double.parseDouble(param[0]);
        action.hSpeed = Double.parseDouble(param[1]);
        action.vAcceleration = Double.parseDouble(param[2]);
        action.hAcceleration = Double.parseDouble(param[3]);
        return action;
    }

    private static Action getTimerMoveAction(GameObject gameObject, String actionParam) {

        TimerMoveAction action = new TimerMoveAction();
        String[] param = actionParam.split(",");
        action.vSpeed = Double.parseDouble(param[0]);
        action.hSpeed = Double.parseDouble(param[1]);
        action.vAcceleration = Double.parseDouble(param[2]);
        action.hAcceleration = Double.parseDouble(param[3]);
        action.moveTime = Integer.parseInt(param[4]);
        return action;
    }

    private static Action getWanderAction(GameObject gameObject, String actionParam) {

        WanderAction action = new WanderAction();
        String[] param = actionParam.split(",");
        action.vSpeed = Double.parseDouble(param[0]);
        action.hSpeed = Double.parseDouble(param[1]);
        action.wanderTime = Integer.parseInt(param[2]);
        return action;
    }

    public static class TestAction extends Action {

        int timer = 0;

        @Override
        public void execute(GameObject gameObject) {

            if (timer % 50 == 0) {
                for (int i = 0; i < 60; i++) {
                    GameObject obj = GamePanel.instance.gameObjectManager.objectLibrary[4].clone();
                    obj.x = 400;
                    obj.y = 300;
                    obj.vSpeed = Math.cos(i * (6 + timer / 50));
                    obj.hSpeed = Math.sin(i * (6 + timer / 50));
                    GamePanel.instance.gameObjectManager.objectList.add(obj);
                }
            }
        }

        @Override
        public boolean finished() {

            return ++timer > 150;
        }
    }

    private static Action test(GameObject gameObject, String actionParam) {

        return new TestAction();
    }

}