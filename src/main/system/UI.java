package main.system;

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
    public boolean showPos = false;

    public UI (GamePanel gp) {

        this.gp = gp;
        ariel40 = new Font("宋体", Font.BOLD, 30);
    }

    public void draw(Graphics2D g2) {

        this.g2 = g2;
        g2.setFont(ariel40);
        g2.setColor(Color.ORANGE);
        if (gp.gameState == gp.TITLE_STATE) drawTitleScreen();
        if (gp.gameState == gp.PLAY_STATE)  drawPlayScreen();
        if (gp.gameState == gp.PAUSE_STATE)  drawPauseScreen();
        if (gp.gameState == gp.FAILED_STATE) drawFailedScreen();
        if (gp.gameState == gp.SUCCESS_STATE) drawSucceedScreen();
        drawDebugScreen();
    }

    // for design
    private void drawDebugScreen() {

        if (showPos) {
            g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12F));
            for (int i = 0; i < 25; i++) { drawText(new DecimalFormat("#000").format(i * 32), i * 32 , 24, Color.BLUE);}
            for (int i = 0; i < 20; i++) { drawText(i * 32 + "", 16, i * 32, Color.BLUE);}
        }
    }

    private void drawSucceedScreen() {

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 80F));
        drawTextAtCenter("恭喜通关！", 200, new Color(255, 215, 0));
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 32F));
        int minutes = (int)playTime / 60;
        drawTextAtCenter("重试次数：" + gp.player.retryNum + ", 耗时: " + minutes + ":" + dFormat.format(playTime - minutes * 60), 250, Color.green);
        if (gp.difficulty == 3 && gp.startLevel == 1) drawTextAtCenter("困难模式挑战成功，请找@dylanou领取小黑子奖励一份", 300, Color.BLACK); // 彩蛋
    }

    public void drawFailedScreen() {

        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 80F));
        drawTextAtCenter("按 R 键再来一次", 300, Color.WHITE);
    }

    public void drawPlayScreen() {

        g2.drawString("重试： " + gp.player.retryNum + "次", 20, 40);
        playTime += (double)1/ Constant.FPS;
        int minutes = (int)playTime / 60;
        g2.drawString("Time: " + minutes + ":" + dFormat.format(playTime - minutes * 60), 550, 40);
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
        g2.drawImage(gp.player.img.getImg(), gp.screenWidth / 2 - 80, gp.screenHeight / 2 - 120, 100, 100, null);

        // 菜单
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 40F));
        drawText(">", 240, 300 + currentCommand * 60, Color.YELLOW);
        drawText( "开始冒险", 290,360, Color.RED);
        drawText( "关卡: " + (gp.currentLevel == 10 ? "BOSS" : gp.currentLevel), 290, 420, Color.BLUE);
        StringBuilder difficultDescribe = new StringBuilder("难度: ☆");
        for (int i = 1; i < gp.difficulty; i++) difficultDescribe.append("☆");
        drawText( difficultDescribe.toString() , 290,480, Color.BLUE);
        drawText( "音效: " + (gp.sound.muted ? "关" : "开") , 290,540, Color.BLUE);

        // 操作说明
        g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 20F));
        drawTextAtCenter("操作说明: 左右方向/AD-移动 空格/SHIFT-跳跃 P-暂停 R-重置 M-音效", 600, Color.CYAN);
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