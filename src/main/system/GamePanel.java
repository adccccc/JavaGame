package main.system;

import main.entity.GameObjectManager;
import main.entity.Player;
import main.entity.TileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {

    // 全局单例
    public static final GamePanel instance = new GamePanel();
    // 画面设置
    public final int originalTileSize = 16; // 16x16 tile
    public final int scale = 2;

    public final int tileSize = originalTileSize * scale; // 32 * 32;
    public final int maxScreenCol = 25;
    public final int maxScreenRow = 20;
    public final int screenWidth = tileSize * maxScreenCol; // 800 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 640 pixels

    // 游戏状态
    public int currentLevel = 1; // 当前关卡
    public final int TOTAL_LEVEL = 10; // 总关卡数目
    public int difficulty = 3; // 当前游戏难度
    public final int MAX_DIFFICULTY = 3; // 总难度级别
    public final int TITLE_STATE = 0, PLAY_STATE = 1, PAUSE_STATE =  2, FAILED_STATE = 3;
    public int gameState = TITLE_STATE; // 当前游戏状态

    public int playerInitX = 0, playerInitY = 0; // 小黑子的初始坐标, 保存在每关的物品配置的第一行

    // SYSTEM
    public UI ui = new UI(this);
    public TileManager tileManager = new TileManager(this);
    public GameObjectManager gameObjectManager = new GameObjectManager(this);
    public KeyHandler keyHandler = new KeyHandler(this);
    public Sound sound = new Sound();
    Thread gameThread;

    // ENTITY
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
        resetLevel(true);
        sound.changeBgm(sound.titleBgm);

        // 游戏线程启动
        (gameThread = new Thread(this)).start();
    }

    public void startGame() {

        gameState = PLAY_STATE;
        sound.changeBgm(sound.gameBgm);
        try {
            resetLevel(true);
        } catch (Exception e) {} // do nothing
    }

    public void changePause() {this.gameState = this.gameState == PAUSE_STATE ? PLAY_STATE : PAUSE_STATE;}

    public void nextLevel() {

        // TODO 这里可以做过关特效
        currentLevel++;
        playerInitX = playerInitY = 0; // 清除起始位置
        resetLevel(true);
        sound.playEffect(sound.nextLevel); // 通关语音
    }

    // 通关
    // TODO 这里做完结内容
    public void gamePassed() {}

    /**
     * 加载地图
     * 新关卡 / 死亡重置时使用
     */
    public void resetLevel(boolean reloadMap){

        try {
            gameObjectManager.reloadGameObject(this.currentLevel); // 重新加载游戏物体
            if (reloadMap) tileManager.loadMap(this.currentLevel); // 跳关时加载地图
            player.resetProperties(); // 重置人物属性
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
        long currentTime,lastTime = System.currentTimeMillis();

        while (gameThread != null) {
            currentTime = System.currentTimeMillis();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                // 1. UPDATE:  element position/status...
                update();
                // 2. DRAW: draw the screen
                repaint();
                delta--;
            }
        }
    }

    /**
     * 先计算物体，再计算角色
     */
    public void update() {
        if (gameState == PLAY_STATE) {
            gameObjectManager.update();
            player.update();
        }
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (gameState == TITLE_STATE) { } // 标题栏不画场景
        else {
            tileManager.draw(g2); // 背景层
            gameObjectManager.draw(g2); // 物品层
            player.draw(g2); // 人物
        }
        ui.draw(g2);
        g2.dispose();
    }

}
