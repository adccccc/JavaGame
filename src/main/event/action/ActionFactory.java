package main.event.action;

import main.entity.GameObject;
import main.system.GamePanel;

public class ActionFactory {

    /**
     * Action静态工厂方法，通过文本参数初始化
     */
    public static Action getAction(GameObject gameObject, String actionName, String actionParam, Action.Trigger trigger) {

        Action action = null;
        try {
            if ("speedChange".equals(actionName)) action = new SpeedChangeAction(gameObject, actionParam);
            if ("timerMove".equals(actionName)) action = new TimerMoveAction(gameObject, actionParam);
            if ("wander".equals(actionName)) action = new WanderAction(gameObject, actionParam);
            if ("test".equals(actionName)) action = new HeartBulletAction(gameObject, actionParam);
            if ("gameOver".equals(actionName)) action = new Action() {public void execute(GameObject gameObject) { GamePanel.instance.gamePassed(); }}; // 游戏结束事件
            if (action != null) {
                action.gameObject = gameObject;
                action.trigger = trigger;
            }
        } catch (Exception e) { // for debug
            e.printStackTrace();
        }
        return action;
    }

    // ------------------- 下面是各Action实现类 -------------------------

    /**
     * 修改物体速度
     * 不能同时修改速度和加速度，不然速度会被覆盖
     */
    public static class SpeedChangeAction extends Action {

        public double vSpeed, hSpeed, vAcceleration, hAcceleration;

        public SpeedChangeAction(GameObject gameObject, String actionParam) {

            String[] param = actionParam.split(",");
            vSpeed = Double.parseDouble(param[0]);
            hSpeed = Double.parseDouble(param[1]);
            vAcceleration = Double.parseDouble(param[2]);
            hAcceleration = Double.parseDouble(param[3]);
        }

        public void execute(GameObject gameObject) { gameObject.vSpeed = vSpeed; gameObject.hSpeed = hSpeed; gameObject.vAcceleration = vAcceleration; gameObject.hAcceleration = hAcceleration; }
    }

    /**
     * 物体移动，增加时间长度
     */
    public static class TimerMoveAction extends SpeedChangeAction {

        public int moveTime = -1;
        private int timer = 0;

        public TimerMoveAction(GameObject gameObject, String actionParam) {

            super(gameObject, actionParam);
        }

        // 移动一定时间
        public void execute(GameObject gameObject) {

            if (timer++ > moveTime) gameObject.stopMoving();
            else super.execute(gameObject);
        }

        public boolean finished() {return timer > moveTime; }
    }

    /**
     * 区域间徘徊
     */
    public static class WanderAction extends Action {

        public double vSpeed, hSpeed;
        public int wanderTime;
        private int count = 0;

        public WanderAction(GameObject gameObject, String actionParam) {

            String[] param = actionParam.split(",");
            vSpeed = Double.parseDouble(param[0]);
            hSpeed = Double.parseDouble(param[1]);
            wanderTime = Integer.parseInt(param[2]);
        }

        // 在两个点之间徘徊
        public void execute(GameObject gameObject) {

            if (++count % (2 * wanderTime) >= wanderTime) {
                gameObject.vSpeed = vSpeed; gameObject.hSpeed = hSpeed;
            } else {
                gameObject.vSpeed = -vSpeed; gameObject.hSpeed = -hSpeed;
            }
        }
    }

    /**
     * 撞墙反弹, 专为BOSS弹幕设计
     */
    public static class ReboundAction extends Action {

        private int count;

        public void execute(GameObject gameObject) {

            if (gameObject.x <= 32 || gameObject.x >= 768) {gameObject.hSpeed *= -1; count--;}
            if (gameObject.y <= 32 || gameObject.y >= 616) {gameObject.vSpeed *= -1; count--;}
        }

        public boolean finished() { return count > 0; }
    }

    /**
     * 心形曲线子弹
     */
    public static class HeartBulletAction extends Action {

        int timer = 0;

        public HeartBulletAction(GameObject gameObject, String actionParam) {}

        public void execute(GameObject gameObject) {

            if (timer % 50 == 0) {

                for (int i = 0; i < 30; i++) {
                    GameObject obj = GamePanel.instance.gameObjectManager.objectLibrary[11].clone();
                    obj.x = 400;
                    obj.y = 50 + timer / 2.0;
                    double theta = i * 12 * Math.PI / 180;
                    obj.vSpeed = -0.1 + (1 + Math.cos(theta)) * Math.cos(theta) * 0.8;
                    obj.hSpeed = (1 + Math.cos(theta)) * Math.sin(theta) * 0.6;
                    GamePanel.instance.gameObjectManager.waitToAddList.add(obj);
                }
            }

//            GameObject obj = GamePanel.instance.gameObjectManager.objectLibrary[4].clone();
//            obj.x = 400;
//            obj.y = 300;
//            obj.vSpeed = Math.cos(timer / 15.0 * Math.PI) * 1.5;
//            obj.hSpeed = Math.sin(timer / 15.0 * Math.PI) * 1.5;
//            GamePanel.instance.gameObjectManager.waitToAddList.add(obj);
        }

        public boolean finished() {

            return ++timer > 300;
        }
    }

}