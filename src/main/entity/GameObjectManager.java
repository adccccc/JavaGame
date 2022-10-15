package main.entity;

import java.util.LinkedList;
import java.util.List;

public class GameObjectManager {

    public GameObject[] objectLibrary; // 游戏物体类型库,通过下标来获取物品
    public List<GameObject> objectList; // 当前存在的物体列表

    public GameObjectManager() {

        this.objectLibrary = new GameObject[10];
        objectList = new LinkedList<>(); // 用链表,删除速度快
        loadLibrary(); // 加载物品库
    }

    private void loadLibrary() {



    }

    // private void setup(int index, String imageName, )

    public void reloadGameObject() {}
}