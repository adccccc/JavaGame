package main.entity;

import main.Constant;
import main.event.effect.CollisionEffect;
import main.system.GamePanel;
import main.system.collision.CollisionChecker;
import main.system.collision.shape.Circle;
import main.system.collision.shape.Polygon;
import main.system.collision.shape.Vector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;

public class GameObjectManager {

    public GamePanel gp;
    public GameObject[] objectLibrary; // 游戏物体类型库,通过下标来获取物品
    public List<GameObject> objectList; // 当前存在的物体列表

    public GameObjectManager(GamePanel gp) {

        this.gp = gp;
        this.objectLibrary = new GameObject[10];
        objectList = new LinkedList<>(); // 用链表,删除速度快
        loadLibrary(); // 加载物品库
    }

    private void loadLibrary() {

        Polygon triangle = new Polygon(new Vector(0, Constant.TILE_SIZE-1), new Vector(Constant.TILE_SIZE-1, Constant.TILE_SIZE-1), new Vector((Constant.TILE_SIZE-1) / 2.0, 0));
        setup(0, "spike_up.png", Constant.TILE_SIZE, Constant.TILE_SIZE, false, false, 0, triangle, null, CollisionEffect.HURT_PLAYER);
        setup(1, "spike_down.png", Constant.TILE_SIZE, Constant.TILE_SIZE, false, false, 0, triangle, null, CollisionEffect.HURT_PLAYER);
        setup(2, "spike_left.png", Constant.TILE_SIZE, Constant.TILE_SIZE, false, false, 0, triangle, null, CollisionEffect.HURT_PLAYER);
        setup(3, "spike_right.png", Constant.TILE_SIZE, Constant.TILE_SIZE, false, false, 0, triangle, null, CollisionEffect.HURT_PLAYER);
    }

    private void setup(int index, String imageName, int width, int height, boolean isSolid, boolean isPlatform, int shape, Polygon poly, Circle circle, CollisionEffect effect) {

        try {
            objectLibrary[index] = new GameObject();
            objectLibrary[index].img = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + imageName)));
            objectLibrary[index].width = width;
            objectLibrary[index].height = height;
            objectLibrary[index].isSolid = isSolid;
            objectLibrary[index].isPlatform = isPlatform;
            objectLibrary[index].shape = shape;
            objectLibrary[index].collisionPoly = poly == null ? new Polygon(width, height) : poly;
            objectLibrary[index].collisionCircle = circle;
            objectLibrary[index].collisionEffect = effect == null ? CollisionEffect.NOTHING : effect;
        } catch (Exception e) {}
    }

    public void reloadGameObject(int level) {

        try {
            InputStream in = getClass().getResourceAsStream("/maps/object" + level + ".csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            int col = 0, row = 0;
            while (row < gp.maxScreenRow) {
                String line = br.readLine();
                while (col < gp.maxScreenCol) {
                    String[] arr = line.split(",");
                    int num = Integer.parseInt(arr[col++]);
                    if (num < 0) continue;
                    GameObject gameObject = objectLibrary[num].clone();
                    gameObject.x = col * Constant.TILE_SIZE;
                    gameObject.y = row * Constant.TILE_SIZE;
                    objectList.add(gameObject);
                }

                col = 0;
                row++;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {

        ListIterator<GameObject> iter = objectList.listIterator();
        while (iter.hasNext()) {
            GameObject obj = iter.next();
            if (checkObjectOffMap(obj)) { // 物体被移除
                iter.remove();
                continue;
            }
            obj.checkAndExecuteAction(); // 执行物体动作
            obj.reCalcSpeed(); // 重新计算物体速度
            CollisionChecker.checkGameObject(gp.player, obj); // 检查碰撞
            obj.reCalcLocation(); // 重新计算物体位置
        }
    }

    // 画出所有物体
    public void draw(Graphics2D g2) { for (GameObject obj : objectList) obj.draw(g2); }

    // 检查物体是否出屏幕
    private boolean checkObjectOffMap(GameObject object) { return object.removed = object.removed || object.x < -100 || object.x > gp.screenWidth + 100 || object.y < -100 || object.y > gp.screenHeight; }

}