package main.event.action;

import main.entity.GameObject;

public class WanderAction extends Action{

    public double vSpeed, hSpeed;
    public int wanderTime = 100;
    private int count = 0;

    // 在两个点之间徘徊
    public void execute(GameObject gameObject) {

        if (++count % (2 * wanderTime) >= wanderTime) {
            gameObject.vSpeed = vSpeed; gameObject.hSpeed = hSpeed;
        } else {
            gameObject.vSpeed = -vSpeed; gameObject.hSpeed = -hSpeed;
        }
    }
}