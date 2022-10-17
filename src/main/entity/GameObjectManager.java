package main.entity;

import main.system.Constant;
import main.event.action.*;
import main.event.effect.CollisionEffect;
import main.system.*;
import main.system.collision.*;
import main.system.collision.shape.*;
import main.system.collision.shape.Polygon;
import main.system.collision.shape.Vector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GameObjectManager {

    public GamePanel gp;
    public GameObject[] objectLibrary = new GameObject[100]; // 游戏物体类型库,通过下标来获取物品
    public LinkedList<GameObject> objectList = new LinkedList<>(), waitToAddList = new LinkedList<>(); // 当前存在的物体链表

    public GameObjectManager(GamePanel gp) {

        this.gp = gp;
        try {
            loadLibrary(); // 加载物品库
        } catch (Exception e) {}
    }

    /// -------------------------------- 预加载物品库 --------------------------------------------

    private void loadLibrary() throws IOException {

        // 初始化游戏所有物体
        Polygon triangleUp = new Polygon(new Vector(0, Constant.TILE_SIZE-1), new Vector(Constant.TILE_SIZE-1, Constant.TILE_SIZE-1), new Vector((Constant.TILE_SIZE-1) / 2.0, 0));
        Polygon triangleDown = new Polygon(new Vector(0, 0), new Vector(Constant.TILE_SIZE-1, 0), new Vector((Constant.TILE_SIZE-1) / 2.0, Constant.TILE_SIZE - 1));
        Polygon triangleLeft = new Polygon(new Vector(0, Constant.TILE_SIZE-1), new Vector(Constant.TILE_SIZE-1, Constant.TILE_SIZE-1), new Vector((Constant.TILE_SIZE-1) / 2.0, 0));
        Polygon triangleRight = new Polygon(new Vector(0, Constant.TILE_SIZE-1), new Vector(Constant.TILE_SIZE-1, Constant.TILE_SIZE-1), new Vector((Constant.TILE_SIZE-1) / 2.0, 0));
        setup(4, "spike_up.png", Constant.TILE_SIZE, Constant.TILE_SIZE, false, 0, triangleUp, 0, CollisionEffect.HURT_PLAYER);
        setup(5, "spike_down.png", Constant.TILE_SIZE, Constant.TILE_SIZE, false, 0, triangleDown, 0, CollisionEffect.HURT_PLAYER);
        setup(6, "spike_left.png", Constant.TILE_SIZE, Constant.TILE_SIZE, false,  0, triangleLeft, 0, CollisionEffect.HURT_PLAYER);
        setup(7, "spike_right.png", Constant.TILE_SIZE, Constant.TILE_SIZE, false,  0, triangleRight, 0, CollisionEffect.HURT_PLAYER);
        setup(8, "next_level.png", Constant.TILE_SIZE, Constant.TILE_SIZE, false,  0, triangleRight, 0, CollisionEffect.NEXT_LEVEL);
        setup(9, "platform3.png", Constant.TILE_SIZE, Constant.TILE_SIZE, false,  0, new Polygon(Constant.TILE_SIZE, 12), 0, CollisionEffect.PLATFORM);
        setup(10, "grass.png", Constant.TILE_SIZE, Constant.TILE_SIZE, false,  1, null, 12, CollisionEffect.SAVE_POINT);
    }

    private void setup(int index, String imageName, int width, int height, boolean isSolid, int shape, Polygon poly, double collisionRadius, CollisionEffect effect) throws IOException {

        objectLibrary[index] = new GameObject();
        objectLibrary[index].img = new DynamicImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + imageName))));
        objectLibrary[index].width = width;
        objectLibrary[index].height = height;
        objectLibrary[index].isSolid = isSolid;
        objectLibrary[index].shape = shape;
        objectLibrary[index].collisionPoly = poly;
        objectLibrary[index].collisionRadius = collisionRadius;
        objectLibrary[index].collisionEffect = effect == null ? CollisionEffect.NOTHING : effect;
    }

    /// -------------------------------- 地图物体加载 ------------------------------------------

    public void reloadGameObject(int level) throws IOException {

        objectList.clear(); // 先清除物体列表
        loadGameObjectFromTextConfig(level); // 从文本配置中加载独立配置的物体
        loadGameObjectFromCsv(level); // 从csv表格中加载批量物体
    }

    /**
     * 从CSV表格中创建游戏物体
     * 物体库中标准物体浅拷贝而来，因此不能带事件和碰撞触发器
     */
    public void loadGameObjectFromCsv(int level) throws IOException {

        InputStream in = getClass().getResourceAsStream("/maps/object" + level + ".csv");
        if (in == null) // 找不到文件
            return;

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        int col = 0, row = 0;
        while (row < gp.maxScreenRow) {
            String line = br.readLine();
            while (col < gp.maxScreenCol) {
                String[] arr = line.split(",");
                int num = Integer.parseInt(arr[col++]);
                if (num <= 0) continue;
                GameObject gameObject = objectLibrary[num].clone();
                gameObject.x = (col-1) * Constant.TILE_SIZE;
                gameObject.y = (row) * Constant.TILE_SIZE;
                objectList.add(gameObject);
            }

            col = 0;
            row++;
        }
        br.close();
    }

    /**
     * 从文本配置文件中创建物体
     * 一般为有特质的物体
     */
    public void loadGameObjectFromTextConfig(int level) throws IOException {

        InputStream in = getClass().getResourceAsStream("/objects/tpl" + level + ".conf");
        if (in == null) // 找不到文件
            return;

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        Map<String, String> propMap = new HashMap<>();
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) {
                objectList.add(createGameObjectByPropMap(propMap));
                propMap.clear();
            } else if (line.startsWith("--")) { // 起始坐标也在这个文件保存
                String[] pos = line.substring(2).split(",");
                if (gp.playerInitX == 0 && gp.playerInitY == 0) { // 坐标没有初始化时加载，否则继续使用存档点坐标
                    gp.playerInitX = Integer.parseInt(pos[0]);
                    gp.playerInitY = Integer.parseInt(pos[1]);
                }
            } else {
                String[] prop = line.split("=");
                propMap.put(prop[0].trim(), prop[1].trim());
            }
        }

        if (!propMap.isEmpty()) objectList.add(createGameObjectByPropMap(propMap));
    }

    public GameObject createGameObjectByPropMap(Map<String, String> propMap) {

        // 从物体库中clone一个标准物体
        GameObject gameObject = objectLibrary[Integer.parseInt(propMap.get("index"))].clone();
        if (propMap.containsKey("x")) gameObject.x = Double.parseDouble(propMap.get("x"));
        if (propMap.containsKey("y")) gameObject.y = Double.parseDouble(propMap.get("y"));
        if (propMap.containsKey("width")) gameObject.width = Integer.parseInt(propMap.get("width"));
        if (propMap.containsKey("height")) gameObject.height = Integer.parseInt(propMap.get("height"));
        if (propMap.containsKey("hSpeed")) gameObject.hSpeed = Double.parseDouble(propMap.get("hSpeed"));
        if (propMap.containsKey("vSpeed")) gameObject.vSpeed = Double.parseDouble(propMap.get("vSpeed"));
        if (propMap.containsKey("hAcceleration")) gameObject.hAcceleration = Double.parseDouble(propMap.get("hAcceleration"));
        if (propMap.containsKey("vAcceleration")) gameObject.vAcceleration = Double.parseDouble(propMap.get("vAcceleration"));
        if (propMap.containsKey("scale")) gameObject.scale = Double.parseDouble(propMap.get("scale"));
        if (propMap.containsKey("rotate")) gameObject.rotate = Integer.parseInt(propMap.get("rotate"));
        if (propMap.containsKey("visible")) gameObject.visible = Boolean.parseBoolean(propMap.get("visible"));
        if (propMap.containsKey("shape")) gameObject.shape = Integer.parseInt(propMap.get("shape"));
        // 从配置中加载碰撞体积
        if (propMap.containsKey("collisionPoly")) gameObject.collisionPoly = new Polygon(Arrays.stream(propMap.get("collisionPoly").split(",")).map(Double::parseDouble).collect(Collectors.toList()));
        if (propMap.containsKey("collisionRadius")) gameObject.collisionRadius = Double.parseDouble(propMap.get("collisionRadius"));
        // 从配置中加载动作和触发器，因为文本配置不好管理，故限制只能有一个动作
        if (propMap.containsKey("actionName")) gameObject.actionList.add(ActionFactory.getAction(gameObject, propMap.get("actionName"), propMap.get("actionParam"), TriggerFactory.getTrigger(gameObject, propMap.get("triggerName"), propMap.get("triggerParam"))));

        return gameObject;
    }

    /// ---------------------------------- 游戏物体状态更新 -----------------------------------------

    public void update() {

        gp.player.onPlatform = false; // 重置角色的平台状态
        for (GameObject obj : objectList) {
            obj.checkAndExecuteAction(); // 执行物体动作
            obj.reCalcSpeed(); // 重新计算物体速度
            CollisionChecker.checkGameObject(gp.player, obj); // 检查角色和物体间的碰撞
            obj.reCalcLocation(); // 重新计算物体位置
        }

        waitToAddList.forEach(newObj -> objectList.addFirst(newObj)); // 新增物体前插到物体
        waitToAddList.clear();
        objectList.removeIf(this::checkObjectOffMap); // 移除失效物体
    }

    // 画出所有物体
    public void draw(Graphics2D g2) {

        // 由于此方法是系统调用，跟游戏线程不一致，因此需要先复制，避免并发修改异常
        List<GameObject> copiedList = new ArrayList<>(objectList);
        for (GameObject obj : copiedList) obj.draw(g2);
    }

    // 检查物体是否出屏幕
    private boolean checkObjectOffMap(Entity object) { return object.removed = object.removed || object.x < -100 || object.x > gp.screenWidth + 100 || object.y < -100 || object.y > gp.screenHeight; }
}