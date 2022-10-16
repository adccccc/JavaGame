package main.system;

import main.Constant;
import main.entity.GameObjectManager;
import main.entity.Player;
import main.entity.TileManager;

import javax.swing.*;
import java.awt.*;

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
    public int currentLevel; // 当前关卡
    public final int TOTAL_LEVEL = 10; // 总关卡数目
    public int difficulty; // 当前游戏难度
    public final int MAX_DIFFICULTY = 3; // 总难度级别
    public int gameState; // 当前游戏状态
    public final int TITLE_STATE = 0;
    public final int PLAY_STATE = 1;
    public final int PAUSE_STATE = 2;
    public final int FAILED_STATE = 3;

    public int playerInitX, playerInitY; // 小黑子的初始坐标

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
            this.setDoubleBuffered(true); // better rendering performance
            this.addKeyListener(keyHandler);
            this.setFocusable(true);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void setupGame() {

        // 初始化配置
        gameState = TITLE_STATE;
        difficulty = 3; // 初始难度选项
        currentLevel = 1; // 初始关卡选项

        // 加载地图
        resetLevel(true);
        sound.changeBgm(sound.titleBgm);
    }

    public void startPanelThread() { (gameThread = new Thread(this)).start(); }

    public void startGame() {

        gameState = PLAY_STATE;
        sound.changeBgm(sound.gameBgm);
        resetLevel(true);
    }

    public void changePause() {this.gameState = this.gameState == PAUSE_STATE ? PLAY_STATE : PAUSE_STATE;}

    public void nextLevel() {

        // TODO 这里可以做通关特效
        currentLevel++;
        resetLevel(true);
        sound.playEffect(sound.nextLevel); // 通关语音
    }

    /**
     * 加载地图
     * 新关卡 / 死亡重置时使用
     */
    public void resetLevel(boolean reloadMap) {

        playerInitX = 100; // 这里要读配置
        playerInitY = 100;
        player.resetProperties();
        gameObjectManager.reloadGameObject(this.currentLevel); // 重新加载游戏物体
        if (reloadMap) tileManager.loadMap(this.currentLevel); // 跳关时加载地图
    }

    @Override
    public void run() {

        double drawInterval = 1000d / Constant.FPS; // 20ms
        double delta = 0;
        long lastTime = System.currentTimeMillis();
        long currentTime;

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

    public void update() {
        if (gameState == PLAY_STATE) {
            player.update();
            gameObjectManager.update();
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (gameState == TITLE_STATE) { }
        else {
            tileManager.draw(g2); // 背景层
            gameObjectManager.draw(g2); // 物品层
            player.draw(g2); // 人物
        }
        ui.draw(g2);
        g2.dispose();
    }

}
