package main.event.action;

import main.entity.GameObject;
import main.system.GamePanel;
import main.system.collision.shape.CcVector;

import java.util.Random;

public class ActionFactory {

    /**
     * Action静态工厂方法，通过文本参数初始化
     */
    public static Action getAction(GameObject gameObject, String actionName, String actionParam, Action.Trigger trigger) {

        Action action = null;
        try {
            if ("disappear".equals(actionName)) action = new DisappearAction(gameObject, actionParam);
            if ("speedChange".equals(actionName)) action = new SpeedChangeAction(gameObject, actionParam);
            if ("timerMove".equals(actionName)) action = new TimerMoveAction(gameObject, actionParam);
            if ("wander".equals(actionName)) action = new WanderAction(gameObject, actionParam);
            if ("gameOver".equals(actionName)) action = new Action() {public void execute(GameObject gameObject) { GamePanel.instance.gamePassed(); }}; // 游戏结束事件
            if ("shapeBullet".equals(actionName)) action = new ShapeBulletAction(gameObject, actionParam);
            if (action != null) { action.gameObject = gameObject; action.trigger = trigger;}
        } catch (Exception ignored) {}

        return action;
    }

    // ------------------- 下面是各Action实现类 -------------------------

    /**
     * 定时移除
     */
    public static class DisappearAction extends Action {

        public int lifeTime;

        public DisappearAction(GameObject gameObject, String actionParam) {lifeTime = Integer.parseInt(actionParam);}

        protected void execute(GameObject gameObject) { if (gameObject.surviveTime > lifeTime) gameObject.removed = true; }
    }

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

        public int moveTime = 0, timer = 0;

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
        public void execute(GameObject gameObject) { gameObject.speed = (++count % (2 * wanderTime) >= wanderTime) ? new CcVector(speed) : new CcVector(-speed.x, -speed.y); }
    }

    /**
     * BOSS弹幕绘制
     * 支持多种图形
     */
    public static class ShapeBulletAction extends Action {

        private final int totalBullet, bulletIndex;  // 总弹幕数和子弹类型
        private String shape; // 形状
        private double speedRatio = 1.0;
        Random random = new Random(System.nanoTime());

        public ShapeBulletAction(GameObject gameObject, String actionParam) {

            String[] arr = actionParam.split(",");
            totalBullet = Integer.parseInt(arr[0]);
            bulletIndex = Integer.parseInt(arr[1]);
            shape = arr[2];
            if (arr.length > 3) speedRatio = Double.parseDouble(arr[3]);
        }

        public void execute(GameObject gameObject) {

            GameObject[] bullets = new GameObject[totalBullet];

            for (int i = 0; i < totalBullet; i++) {
                bullets[i] = GamePanel.instance.gameObjectManager.objectLibrary[bulletIndex].clone(); // 创建弹幕
                GamePanel.instance.gameObjectManager.waitToAddList.add(bullets[i]); // 加到下一帧
                bullets[i].pos = gameObject.pos.plus(new CcVector(gameObject.box.x / 2.0, gameObject.box.y / 2.0)); // 起始位置为物体中心
                double theta = i * 360.0 / totalBullet * Math.PI / 180; // 极角

                // ------ 计算各形状的初始速度 ------
                if ("circle".equals(shape)) { // 圆形扩散
                    bullets[i].speed = new CcVector(Math.cos(theta), Math.sin(theta));
                } else if ("rect".equals(shape)) { //  方形扩散
                    int sc = totalBullet / 4;
                    if (i / sc == 2 || i / sc == 0) {
                        bullets[i].speed.y = i >= 1.5 * sc ? speedRatio : -speedRatio;
                        bullets[i].speed.x = (i >= sc * 0.5 && i < (sc * 2.5) ? -1 : 1) * Math.abs((i % sc - 0.5 * sc) * 2 / sc) * speedRatio;
                    } else {
                        bullets[i].speed.x = i >= 2.5 * sc ? speedRatio : -speedRatio;
                        bullets[i].speed.y = (i >= sc * 1.5 && i < (sc * 3.5) ? 1 : -1) * Math.abs((i % sc - 0.5 * sc) * 2 / sc) * speedRatio;
                    }
                } else if ("heart".equals(shape)) { // 心形扩散
                    // 心形曲线计算公式； ρ(Theta) = α(1+cosTheta)
                    bullets[i].speed = new CcVector((1 + Math.cos(theta)) * Math.sin(theta) * speedRatio * 0.7, (1 + Math.cos(theta)) * Math.cos(theta) * speedRatio);
                } else if ("random".equals(shape)) { // 随机扩散
                    bullets[i].speed = new CcVector(random.nextDouble() * (random.nextBoolean() ? 1 : -1), random.nextDouble() * (random.nextBoolean() ? 1 : -1));
                }

            }
        }

        public boolean finished() { return triggered; }
    }

}