package main.entity;

import main.event.action.Action;
import main.event.effect.CollisionEffect;
import main.system.collision.shape.Circle;
import main.system.collision.shape.Polygon;
import main.system.collision.shape.Vector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

public class GameObject extends Entity {

    // 是否阻拦人物
    public boolean isSolid = false;
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
            cloneObj.actionList = new ArrayList<>(this.actionList);
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
                action.execute(this);
            }
        }
    }

    public void onCollision(Player player) {

        if (collisionEffect == null) return;
        collisionEffect.onCollision(this, player);
    }

}
