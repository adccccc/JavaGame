package main.entity;

import main.system.Constant;
import main.event.action.*;
import main.event.effect.CollisionEffect;
import main.system.*;
import main.system.collision.*;
import main.system.collision.shape.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GameObjectManager {

    public GamePanel gp;
    public GameObject[] objectLibrary = new GameObject[100]; // 游戏物体类型库,通过下标来获取物品
    public LinkedList<GameObject> objectList = new LinkedList<>(), waitToAddList = new LinkedList<>(); // 当前存在的物体链表

    public GameObjectManager(GamePanel gp) {

        this.gp = gp;
        try { loadLibrary();  } catch (Exception ignored) { } // 加载物品库, debug时打错误日志
    }

    /// -------------------------------- 预加载物品库 --------------------------------------------

    private void loadLibrary() throws IOException {

        // 初始化游戏所有物体
        CcPolygon triangleUp = new CcPolygon(new CcVector(0, Constant.TILE_SIZE-1), new CcVector(Constant.TILE_SIZE-1, Constant.TILE_SIZE-1), new CcVector((Constant.TILE_SIZE-1) / 2.0, 0));
        CcPolygon triangleDown = new CcPolygon(new CcVector(0, 0), new CcVector(Constant.TILE_SIZE-1, 0), new CcVector((Constant.TILE_SIZE-1) / 2.0, Constant.TILE_SIZE - 1));
        CcPolygon triangleLeft = new CcPolygon(new CcVector(0, Constant.TILE_SIZE-1), new CcVector(Constant.TILE_SIZE-1, Constant.TILE_SIZE-1), new CcVector((Constant.TILE_SIZE-1) / 2.0, 0));
        CcPolygon triangleRight = new CcPolygon(new CcVector(0, Constant.TILE_SIZE-1), new CcVector(Constant.TILE_SIZE-1, Constant.TILE_SIZE-1), new CcVector((Constant.TILE_SIZE-1) / 2.0, 0));
        setup(4,  Constant.TILE_SIZE, Constant.TILE_SIZE, 0, new CcPolygon(Constant.TILE_SIZE, 14), 0, CollisionEffect.PLATFORM, null, 1, "platform3.png");
        setup(5, Constant.TILE_SIZE, Constant.TILE_SIZE, 0, triangleUp, 0, CollisionEffect.HURT_PLAYER, null,1, "spike_up.png");
        setup(6,  Constant.TILE_SIZE, Constant.TILE_SIZE, 0, triangleDown, 0, CollisionEffect.HURT_PLAYER, null,1, "spike_down.png");
        setup(7,  Constant.TILE_SIZE, Constant.TILE_SIZE,  0, triangleLeft, 0, CollisionEffect.HURT_PLAYER, null,1, "spike_left.png");
        setup(8,  Constant.TILE_SIZE, Constant.TILE_SIZE,   0, triangleRight, 0, CollisionEffect.HURT_PLAYER, null,1, "spike_right.png");
        setup(9,  Constant.TILE_SIZE, Constant.TILE_SIZE,  1, null, 16, CollisionEffect.SAVE_POINT, null,1, "save_point.png");
        setup(10,  Constant.TILE_SIZE, Constant.TILE_SIZE,   0, triangleRight, 0, CollisionEffect.NEXT_LEVEL, null,1, "next_level.png");
        setup(11,  16, 16,  1, null, 7, CollisionEffect.HURT_PLAYER, null, 3, "bullet_fire_1.png", "bullet_fire_2.png");
        setup(12,  16, 16,  1, null, 7, CollisionEffect.HURT_PLAYER, ActionFactory.getAction(null, "disappear", "800", Action.Trigger.IMMEDIATE),3, "bullet_heart.png");
        setup(13,  32, 32,  1, null, 10, CollisionEffect.HURT_PLAYER, null, 15, "basketball_0.png","basketball_1.png");
        setup(14,  Constant.TILE_SIZE, Constant.TILE_SIZE, 0, null, 0, CollisionEffect.NOTHING, null, 1, "wall_stone.png"); // 掩体
        setup(15,  Constant.TILE_SIZE, Constant.TILE_SIZE, 0, null, 0, CollisionEffect.NOTHING, null,1, "wall_stone.png"); // 掩体
        setup(16,  20, 20,  1, null, 7, CollisionEffect.HURT_PLAYER, null,3, "bullet_blue_0.png","bullet_blue_1.png");
        setup(17,  32, 32,  1, null, 7, CollisionEffect.HURT_PLAYER, ActionFactory.getAction(null, "disappear", "500", Action.Trigger.IMMEDIATE),5, "bullet_smile_0.png","bullet_smile_1.png","bullet_smile_2.png"); // 随机子弹

        // BOSS展示对象
        setup(98, 180,180,0,null,0, CollisionEffect.HURT_PLAYER, null, 6, "boss_00.png","boss_01.png","boss_02.png","boss_03.png","boss_04.png","boss_05.png","boss_06.png","boss_07.png","boss_08.png","boss_09.png","boss_10.png","boss_11.png","boss_12.png");
        // 专用隐形对象
        setup(99,  1, 1,  1, null, 1, CollisionEffect.NOTHING, null,1, "water.png");
    }

    private void setup(int index, int width, int height, int shape, CcPolygon poly, double collisionRadius, CollisionEffect effect, Action action, int imgFrame, String... imgNames) throws IOException {

        objectLibrary[index] = new GameObject();
        objectLibrary[index].pos = new CcVector(0,0);
        objectLibrary[index].box = new CcVector(width, height);
        objectLibrary[index].img = generateDynamicImg(imgFrame, imgNames);
        objectLibrary[index].shape = shape;
        objectLibrary[index].collisionPoly = poly;
        objectLibrary[index].collisionRadius = collisionRadius;
        objectLibrary[index].collisionEffect = effect == null ? CollisionEffect.NOTHING : effect;
        if (action != null) objectLibrary[index].actionList.add(action);
    }

    // 加载成动图
    public DynamicImage generateDynamicImg(int frame, String... imgName) throws IOException {

        BufferedImage[] imgArr = new BufferedImage[imgName.length];
        for (int i = 0; i < imgArr.length; i++) imgArr[i] = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/" + imgName[i])));
        return new DynamicImage(frame, imgArr);
    }


    /// -------------------------------- 地图物体加载 ------------------------------------------

    public void reloadGameObject(int level) throws IOException {

        objectList = new LinkedList<>(); // 先清除物体列表 (这里不能用clear, 会有ConcurrentModificationException) 但是用new之后也会有小概率遇到
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
                gameObject.pos = new CcVector((col-1) * Constant.TILE_SIZE, (row) * Constant.TILE_SIZE);
                objectList.add(gameObject);
            }

            col = 0; row++;
        }
        br.close();
    }

    /**
     * 从文本配置文件中创建物体
     * 一般为有特质的物体
     */
    public void loadGameObjectFromTextConfig(int level) throws IOException {

        InputStream in = getClass().getResourceAsStream("/objects/tpl" + level + ".txt");
        if (in == null) return; // 找不到文件

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        Map<String, String> propMap = new HashMap<>();

        // 每关配置文件的第一行为重生坐标
        String line = br.readLine();
        // 坐标没有初始化时加载，否则继续使用存档点坐标
        if (gp.playerInitPos.x == 0 && gp.playerInitPos.y == 0) gp.playerInitPos = new CcVector(line);

        // 加载游戏物体配置
        while ((line = br.readLine()) != null) {

            if (line.isEmpty() && !propMap.isEmpty()) {
                objectList.addAll(createGameObjectByPropMap(propMap));
                propMap.clear();
            } else if (!line.startsWith("#")) { // 配置的注释以#开头
                String[] prop = line.split("=");
                propMap.put(prop[0].trim(), prop[1].trim());
            }
        }

        if (!propMap.isEmpty()) objectList.addAll(createGameObjectByPropMap(propMap));
    }

    public List<GameObject> createGameObjectByPropMap(Map<String, String> propMap) {

        List<GameObject> list = new ArrayList<>();
        String[] posList = propMap.get("pos").split(";"); // 位置不同的批量物品
        for (String pos : posList) {
            // 从物体库中clone一个标准物体
            GameObject gameObject = objectLibrary[Integer.parseInt(propMap.get("index"))].clone();
            if (propMap.containsKey("speed")) gameObject.speed = new CcVector(propMap.get("speed"));
            if (propMap.containsKey("pos")) gameObject.pos = new CcVector(pos);
            if (propMap.containsKey("box")) gameObject.box = new CcVector(propMap.get("box"));
            if (propMap.containsKey("scale")) gameObject.scale = Double.parseDouble(propMap.get("scale"));
            // if (propMap.containsKey("rotate")) gameObject.rotate = Integer.parseInt(propMap.get("rotate"));
            if (propMap.containsKey("visible")) gameObject.visible = Boolean.parseBoolean(propMap.get("visible"));
            // if (propMap.containsKey("shape")) gameObject.shape = Integer.parseInt(propMap.get("shape"));
            // 从配置中加载碰撞体积
            // if (propMap.containsKey("collisionPoly")) gameObject.collisionPoly = new CcPolygon(Arrays.stream(propMap.get("collisionPoly").split(",")).map(Double::parseDouble).collect(Collectors.toList()));
            // if (propMap.containsKey("collisionRadius")) gameObject.collisionRadius = Double.parseDouble(propMap.get("collisionRadius"));
            // 从配置中加载动作和触发器，因为文本配置不好管理，故限制只能有一个动作
            if (propMap.containsKey("actionName")) gameObject.actionList.add(ActionFactory.getAction(gameObject, propMap.get("actionName"), propMap.get("actionParam"), TriggerFactory.getTrigger(gameObject, propMap.get("triggerName"), propMap.get("triggerParam"))));

            list.add(gameObject);
        }
        return list;
    }

    // ---------------------------------- 游戏物体状态更新 -----------------------------------------

    public void update() {

        gp.player.onPlatform = false; // 重置角色的平台状态
        for (GameObject obj : objectList) {
            if (gp.endLoopFlag) { gp.endLoopFlag = false; break; }  // 当前关卡结束，不再更新

            obj.checkAndExecuteAction(); // 执行物体动作
            CollisionChecker.checkGameObject(gp.player, obj); // 检查角色和物体间的碰撞
            obj.reCalcLocation(); // 重新计算物体位置
            obj.surviveTime++; // 存活计时++
        }

        waitToAddList.forEach(newObj -> objectList.addFirst(newObj)); // 新增物体前插到物体
        waitToAddList.clear();
        objectList.removeIf(this::checkObjectOffMap); // 移除失效物体
    }

    // 绘制所有物体
    public void draw(Graphics2D g2) { for (GameObject obj : objectList) obj.draw(g2); }

    // 检查物体是否出屏幕
    private boolean checkObjectOffMap(Entity object) { return object.removed = object.removed || object.pos.x < -100 || object.pos.x > gp.screenWidth + 100 || object.pos.y < -100 || object.pos.y > gp.screenHeight; }
}