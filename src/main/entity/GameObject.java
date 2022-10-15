package main.entity;

import main.event.action.Action;
import main.event.effect.CollisionEffect;
import main.system.collision.shape.Circle;
import main.system.collision.shape.Polygon;
import main.system.collision.shape.Vector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class GameObject extends Entity {

    // 是否阻拦人物
    public boolean isSolid = true;
    // 是否为平台
    public boolean isPlatform = false;
    // 物体形状 0-多边形 1-圆形
    public int shape = 0;

    /**
     * 凸多边形端点列表，两两相连，围成物体的撞检测区域
     * 端点坐标为物体坐标(左上角)的相对坐标，一般情况均为正数
     */
    public Polygon collisionPoly;
    // 圆形碰撞区域
    public Circle collisionCircle;
    // 物体事件
    public List<Action> actionList = new LinkedList<>();
    // 碰撞效果
    public CollisionEffect collisionEffect;

//    public GameObject() {}
//    public GameObject(GameObject another) {
//
//        this.img = another.img;
//        this.x = another.x;
//        this.y = another.y;
//        this.width = another.width;
//        this.height = another.height;
//        this.hSpeed = another.hSpeed;
//        this.vSpeed = another.vSpeed;
//        this.hAcceleration = another.hAcceleration;
//        this.vAcceleration = another.vAcceleration;
//        this.scale = another.scale;
//        this.rotate = another.rotate;
//        this.visible = another.visible;
//        this.removed = another.removed;
//        this.isSolid = another.isSolid;
//        this.isPlatform = another.isPlatform;
//        this.shape = another.shape;
//        this.collisionPoly =
//    }



    public GameObject clone() {

        try {
            return (GameObject) super.clone();
        } catch (Exception e) { return null;}
    }

    // 检查并执行物体附带的动作
    public void checkAndExecuteAction() {

        if (actionList == null || actionList.isEmpty())
            return;

        ListIterator<Action> iter = actionList.listIterator();
        while (iter.hasNext()) {
            Action action = iter.next();
            if (action.finished()) {
                // 执行完毕,从动作列表移除
                iter.remove();
                // 将后继动作添加到动作列表
                if (action.followList != null)
                    for (Action followed : action.followList)
                        iter.add(followed);
            } else if (action.checkTrigger()) {
                // 达到触发条件时，执行动作
                action.execute(this);
            }
        }
    }

    public void onCollision(Player player) {

        if (collisionEffect == null) return;
        collisionEffect.onCollision(this, player);
    }

}
