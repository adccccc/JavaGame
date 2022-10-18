package main.event.action;

import main.entity.GameObject;
import main.system.GamePanel;
import main.system.collision.shape.CcVector;

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
            if ("gameOver".equals(actionName)) action = new Action() {public void execute(GameObject gameObject) { GamePanel.instance.gamePassed(); }}; // 游戏结束事件
            if ("shapeBullet".equals(actionName)) action = new ShapeBulletAction(gameObject, actionParam);
            if ("rebound".equals(actionName)) action = new ReboundAction();
            if (action != null) { action.gameObject = gameObject; action.trigger = trigger;}
        } catch (Exception e) { // for debug
            e.printStackTrace();
        }
        return action;
    }

    // ------------------- 下面是各Action实现类 -------------------------

    /**
     * 修改物体速度
     */
    public static class SpeedChangeAction extends Action {

        public CcVector speed;

        public SpeedChangeAction(GameObject gameObject, String actionParam) {speed = new CcVector(actionParam);}

        public void execute(GameObject gameObject) { gameObject.speed = new CcVector(speed); }
    }

    /**
     * 物体移动，增加时间长度
     */
    public static class TimerMoveAction extends SpeedChangeAction {

        public int moveTime;
        private int timer = 0;

        public TimerMoveAction(GameObject gameObject, String actionParam) {

            super(gameObject, actionParam);
            moveTime = Integer.parseInt(actionParam.split(",")[4]);
        }

        // 移动一定时间
        public void execute(GameObject gameObject) { if (++timer > moveTime) gameObject.stopMoving(); else super.executeAction(gameObject);}

        public boolean finished() {return timer > moveTime;}
    }

    /**
     * 区域间徘徊
     */
    public static class WanderAction extends Action {

        public CcVector speed;
        public int wanderTime, count = 0;

        public WanderAction(GameObject gameObject, String actionParam) {

            String[] param = actionParam.split(",");
            speed = new CcVector(Double.parseDouble(param[0]), Double.parseDouble(param[1]));
            wanderTime = Integer.parseInt(param[2]);
        }

        // 在两个点之间徘徊
        public void execute(GameObject gameObject) {

            gameObject.speed = (++count % (2 * wanderTime) >= wanderTime) ? new CcVector(speed) : new CcVector(-speed.x, -speed.y);
        }
    }

    /**
     * 撞墙反弹, 专为BOSS弹幕设计
     */
    public static class ReboundAction extends Action {

        private int count;

        public void execute(GameObject gameObject) {

            if (gameObject.pos.x <= 32 || gameObject.pos.x >= 768) {gameObject.speed.x *= -1; count--;}
            if (gameObject.pos.y <= 32 || gameObject.pos.y >= 616) {gameObject.speed.y *= -1; count--;}
        }

        public boolean finished() { return count > 0; }
    }

    /**
     * BOSS弹幕绘制
     * 支持多种图形
     */
    public static class ShapeBulletAction extends Action {

        private int totalBullet, bulletIndex; // 总弹幕数和子弹类型
        private String shape; // 形状

        public ShapeBulletAction(GameObject gameObject, String actionParam) {

            String[] arr = actionParam.split(",");
            totalBullet = Integer.parseInt(arr[0]);
            bulletIndex = Integer.parseInt(arr[1]);
            shape = arr[2];
        }

        public void execute(GameObject gameObject) {

            GameObject[] bullets = new GameObject[totalBullet];
            for (int i = 0; i < totalBullet; i++) {
                bullets[i] = GamePanel.instance.gameObjectManager.objectLibrary[bulletIndex].clone(); // 创建弹幕
                GamePanel.instance.gameObjectManager.waitToAddList.add(bullets[i]); // 加到下一帧
                bullets[i].pos = gameObject.pos.plus(new CcVector(gameObject.box.x / 2.0, gameObject.box.y / 2.0)); // 起始位置为物体中心
                // ------ 计算初始速度 ------
                if ("circle".equals(shape)) { // 圆形扩散


                } else if ("rect".equals(shape)) {

                } else if ("triangle".equals(shape)) {

                } else if ("heart".equals(shape)) { // 心形扩散
                    // 心形曲线计算公式； ρ(Theta) = α(1+cosTheta)
                    double theta = i * 360.0 / totalBullet * Math.PI / 180;
                    bullets[i].speed = new CcVector((1 + Math.cos(theta)) * Math.sin(theta) * 0.6,
                            (1 + Math.cos(theta)) * Math.cos(theta) * 0.8);
                }

            }



//            GameObject obj = GamePanel.instance.gameObjectManager.objectLibrary[4].clone();
//            obj.x = 400;
//            obj.y = 300;
//            obj.vSpeed = Math.cos(timer / 15.0 * Math.PI) * 1.5;
//            obj.hSpeed = Math.sin(timer / 15.0 * Math.PI) * 1.5;
//            GamePanel.instance.gameObjectManager.waitToAddList.add(obj);
        }

        public boolean finished() { return triggered; }
    }

}