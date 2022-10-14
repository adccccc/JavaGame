package main.entity;

import main.other.Circle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameObject extends Entity {

    public int collisionMode = 0; // 碰撞模式 0-无 1-顶部平台 2-多边形 3-圆形
    /**
     * 凸多边形端点列表，两两相连，围成物体的撞检测区域
     * 端点坐标为物体坐标(左上角)的相对坐标，一般情况均为正数
     */
    public List<Point> solidArea = new ArrayList<>();
    public Circle solidCircle;  // 圆形碰撞区域
}
