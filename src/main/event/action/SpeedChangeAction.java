package main.event.action;

import main.entity.GameObject;

public class SpeedChangeAction extends Action {

    public double vSpeed, hSpeed, vAcceleration, hAcceleration;

    // 修改物体速度
    // 不能同时修改速度和加速度，不然速度会被覆盖
    public void execute(GameObject gameObject) { gameObject.vSpeed = vSpeed; gameObject.hSpeed = hSpeed; gameObject.vAcceleration = vAcceleration; gameObject.hAcceleration = hAcceleration; }
}
