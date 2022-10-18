package main.entity;

import main.event.action.Action;
import main.event.effect.CollisionEffect;
import main.system.DynamicImage;
import main.system.collision.shape.CcVector;

import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

public class GameObject extends Entity {

    // 物体事件
    public List<Action> actionList = new LinkedList<>();
    // 碰撞效果
    public CollisionEffect collisionEffect;

    // 反射拷贝属性
    public GameObject clone() {

        try {
            GameObject cloneObj = new GameObject();
            Class<?> sClass = this.getClass();
            for (Field field : sClass.getFields()) {
                field.setAccessible(true);
                field.set(cloneObj, field.get(this));
            }
            cloneObj.surviveTime = 0; // 重新计时
            cloneObj.speed = new CcVector(this.speed);
            cloneObj.pos = new CcVector(this.pos);
            cloneObj.box = new CcVector(this.box);
            cloneObj.img = new DynamicImage(this.img.frame, this.img.images); // 新建GIF集
            cloneObj.actionList = new ArrayList<>(this.actionList); // 加载默认事件
            return cloneObj;
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
                action.executeAction(this);
            }
        }
    }

    public void onCollision(Player player) {

        if (collisionEffect == null) return;
        collisionEffect.onCollision(this, player);
    }

}
