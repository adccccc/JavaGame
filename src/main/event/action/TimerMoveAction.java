package main.event.action;


import main.entity.GameObject;

public class TimerMoveAction extends SpeedChangeAction {

    public int moveTime = -1;
    private int timer = 0;

    // 移动一定时间
    public void execute(GameObject gameObject) {

        if (timer++ > moveTime) gameObject.stopMoving();
        else super.execute(gameObject);
    }

    public boolean finished() {return timer > moveTime; }
}
