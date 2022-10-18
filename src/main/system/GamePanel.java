package main.system;

import main.entity.*;
import main.system.collision.shape.CcVector;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    // 全局单例
    public static final GamePanel instance = new GamePanel();

    // 画面设置
    public final int tileSize = Constant.TILE_SIZE; // 32px 的地砖;
    public final int maxScreenCol = 25, maxScreenRow = 20;
    public final int screenWidth = tileSize * maxScreenCol; // 800 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 640 pixels

    // 游戏状态
    public int currentLevel = 1, startLevel = 1; // 当前关卡和起始关卡
    public final int TOTAL_LEVEL = 6; // 总关卡数目
    public int difficulty = 3; // 当前游戏难度
    public final int MAX_DIFFICULTY = 3; // 总难度级别
    public final int TITLE_STATE = 0, PLAY_STATE = 1, PAUSE_STATE =  2, FAILED_STATE = 3, SUCCESS_STATE = 4;
    public int gameState = TITLE_STATE; // 当前游戏状态
    public boolean endLoopFlag = false; // 结束当前帧update的标记，通关后触发
    public CcVector playerInitPos = new CcVector(0, 0); // 小黑子的初始坐标，保存在每关配置文件第一行

    // 系统组件
    public UI ui = new UI(this);
    public TileManager tileManager = new TileManager(this);
    public GameObjectManager gameObjectManager = new GameObjectManager(this);
    public KeyHandler keyHandler = new KeyHandler(this);
    public Sound sound = new Sound();
    Thread gameThread;

    // 角色
    public Player player = new Player(this, keyHandler);

    private GamePanel() {

        try {
            this.setPreferredSize(new Dimension(screenWidth, screenHeight));
            this.setBackground(Color.black);
            this.setDoubleBuffered(true); // 性能提升
            this.addKeyListener(keyHandler);
            this.setFocusable(true);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void setupGame() {

        // 加载地图
        // resetLevel(true);
        sound.changeBgm(sound.titleBgm);
        // 游戏线程启动
        (gameThread = new Thread(this)).start();
    }

    public void startGame() {

        startLevel = currentLevel; // 记录从哪关开始玩的
        sound.changeBgm(sound.gameBgm);
        resetLevel();

        gameState = PLAY_STATE;
    }

    // 暂停状态切换
    public void changePause() {this.gameState = this.gameState == PAUSE_STATE ? PLAY_STATE : PAUSE_STATE;}

    public void nextLevel() {

        // TODO 这里可以做过关特效
        sound.playEffect(sound.nextLevel); // 通关语音
        ++currentLevel;
        playerInitPos = new CcVector(0, 0); // 上一关的重生坐标清除，重新加载
        resetLevel();
    }

    // 通关
    // TODO 这里做完结内容
    public void gamePassed() { this.gameState = SUCCESS_STATE; }

    /**
     * 加载地图
     * 新关卡 / 死亡重置时使用
     */
    public void resetLevel(){

        try {
            endLoopFlag = true; // 结束上一轮的update
            gameObjectManager.reloadGameObject(this.currentLevel); // 重新加载游戏物体
            tileManager.loadMap(this.currentLevel); // 跳关时加载地图
            player.resetProperties(); // 重置人物属性
            if (currentLevel == TOTAL_LEVEL) sound.changeBgm(sound.bossBgm); // 进BOSS换音效
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 核心游戏循环
     * 计算游戏内容 -> 重绘画面
     */
    public void run() {

        double drawInterval = 1000d / Constant.FPS; // 20ms
        double delta = 0;
        long currentTime, lastTime = System.currentTimeMillis();

        while (gameThread != null) {
            currentTime = System.currentTimeMillis();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    /**
     * 重新计算所有物体与角色
     * 先计算物体，再计算角色
     */
    public void update() {

        if (gameState == PLAY_STATE) {
            try {
                gameObjectManager.update();
                player.update();
            } catch (Exception e) { // 必要的catch, 防止某一帧冲突
                e.printStackTrace();
            }
        }
    }

    /**
     * 重新绘制所有内容
     * 背景层 -> 物体层 -> 角色层 -> UI层
     */
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameState != TITLE_STATE) { // 标题栏不画游戏场景

            tileManager.draw(g2); // 背景层
            gameObjectManager.draw(g2); // 物品层
            player.draw(g2); // 人物
        }

        ui.draw(g2); // UI
        g2.dispose();
    }

}
