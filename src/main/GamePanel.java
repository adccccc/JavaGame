package main;

import entity.Player;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    public final int originalTileSize = 16; // 16x16 tile
    public final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48 * 48;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    // FPS
    final int FPS = 60;

    // SYSTEM
    public UI ui = new UI(this);
    public Sound sound = new Sound();
    TileManager tileManager = new TileManager(this);
    KeyHandler keyHandler = new KeyHandler();
    Thread gameThread;
    public CollisionChecker collisionChecker = new CollisionChecker(this);

    // ENTITY
    public Player player = new Player(this, keyHandler);

    public GamePanel() throws IOException {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // better rendering performance
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
    }

    public void startGameThread() {

        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        double drawInterval = 1000d / FPS; // 16.67ms
        double delta = 0;
        long lastTime = System.currentTimeMillis();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.currentTimeMillis();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                // 1. UPDATE:  element position/status...
                update();
                // 2. DRAW: draw the screen
                repaint();
                delta --;
                drawCount++;
            }

            if (timer >= 1000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }

        }
    }

    public void update() {

        player.update();
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        tileManager.draw(g2);
        player.draw(g2);
        ui.draw(g2);
        g2.dispose();
    }

}
