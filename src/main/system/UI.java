package main.system;

import main.Constant;

import java.awt.*;
import java.text.DecimalFormat;

public class UI {

    GamePanel gp;
    Graphics2D g2;
    Font ariel40;
    double playTime;
    DecimalFormat dFormat = new DecimalFormat("#00.00");
    public int currentCommand = 1;
    public final int COMMAND_MAX_NUM = 4;

    public UI (GamePanel gp) {

        this.gp = gp;
        ariel40 = new Font("宋体", Font.BOLD, 30);
    }

    public void draw(Graphics2D g2) {

        this.g2 = g2;
        g2.setFont(ariel40);
        g2.setColor(Color.ORANGE);
        if (gp.gameState == gp.TITLE_STATE) { drawTitleScreen(); }
        if (gp.gameState == gp.PLAY_STATE) { drawPlayScreen(); }
        if (gp.gameState == gp.PAUSE_STATE) { drawPauseScreen(); }
    }

    public void drawPlayScreen() {

        g2.drawString("你干嘛： " + gp.player.retryNum, 20, 40);
        playTime += (double)1/ Constant.FPS;
        int minutes = (int)playTime / 60;
        g2.drawString("Time: " + minutes + ":" + dFormat.format(playTime - minutes * 60), gp.tileSize * 11, 65);
    }

    public void drawTitleScreen() {

        // 背景色填充
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 80F));

        // 标题
        String text = "小黑子大冒险";
        int x = getXForCenteredText(text);
        int y = gp.screenHeight / 4;
        // 增加字体阴影
        drawText(text, x + 5, y + 5, Color.GRAY);
        drawText(text, x, y, Color.WHITE);

        // 像素小人
        g2.drawImage(gp.player.img, gp.screenWidth / 2, gp.screenHeight / 2, 100, 80, null);

        // 菜单
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 40F));
        drawText(">", 240, 380 + currentCommand * 60, Color.YELLOW);
        drawText( "开始冒险", 290,440, Color.RED);
        drawText( "关卡: " + (gp.currentLevel == 10 ? "BOSS" : gp.currentLevel), 290, 500, Color.BLUE);
        StringBuilder difficultDescribe = new StringBuilder("难度: ☆");
        for (int i = 1; i < gp.difficulty; i++) difficultDescribe.append("☆");
        drawText( difficultDescribe.toString() , 290,560, Color.BLUE);
        drawText( "音效: " + (gp.sound.muted ? "关" : "开") , 290,620, Color.BLUE);
    }

    public void drawPauseScreen() {

        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80));
        drawTextAtCenter("PAUSED", gp.screenHeight / 2, null);
    }

    private void drawTextAtCenter(String text, int y, Color color) { drawText(text, getXForCenteredText(text), y, color); }

    private int getXForCenteredText(String text) { return gp.screenWidth / 2 - ((int)g2.getFontMetrics().getStringBounds(text, g2).getWidth()) / 2; }

    private void drawText(String text, int x, int y, Color color) {

        if (color != null) g2.setColor(color);
        g2.drawString(text, x, y);
    }



}
