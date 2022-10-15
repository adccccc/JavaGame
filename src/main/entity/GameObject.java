package main.entity;

import main.event.action.Action;
import main.system.collision.shape.Circle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class GameObject extends Entity {

    public int collisionMode = 0; // 碰撞模式 0-无 1-平台 2-多边形 3-圆形
    /**
     * 凸多边形端点列表，两两相连，围成物体的撞检测区域
     * 端点坐标为物体坐标(左上角)的相对坐标，一般情况均为正数
     */
    public List<Point> solidArea = new ArrayList<>();
    public Circle solidCircle;  // 圆形碰撞区域
    public List<Action> actionList = new LinkedList<>();

    // public GameObject(BufferedImage img, double width, double height, )

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
}
