package main.entity;

import main.system.GamePanel;
import main.tool.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {

    public Tile[] tile;
    public int mapTileNum[][];

    public TileManager() {

        tile = new Tile[10];
        mapTileNum = new int[GamePanel.instance.maxScreenCol][GamePanel.instance.maxScreenRow];
        getTileImage();
    }

    public void getTileImage() {

        setup(0, "grass", false, false);
        setup(1, "wall", true, false);
        setup(2, "water", false, true);
    }

    public void setup(int index, String imageName, boolean collision, boolean platform) {

        try {
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(getClass().getResourceAsStream("/tiles/" + imageName + ".png"));
            tile[index].image = UtilityTool.scaleImage(tile[index].image, GamePanel.instance.tileSize, GamePanel.instance.tileSize);
            tile[index].collision = collision;
            tile[index].platform = platform;
        } catch (Exception e) {}
    }

    public void loadMap(int level) {

        try {
            InputStream in = getClass().getResourceAsStream("/maps/map" + level + ".csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            int col = 0, row = 0;
            GamePanel gp = GamePanel.instance;
            while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
                String line = br.readLine();
                while (col < gp.maxScreenCol) {
                    String[] arr = line.split(",");
                    int num = Integer.parseInt(arr[col]);
                    mapTileNum[col][row] = num;
                    col++;
                }
                if (col == gp.maxScreenCol) {
                    col = 0;
                    row++;
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void draw(Graphics2D g2) {

        int col = 0, row = 0, x = 0, y = 0;
        GamePanel gp = GamePanel.instance;
        while (col < gp.maxScreenCol && row < gp.maxScreenRow) {
            int tileNum = mapTileNum[col][row];
            g2.drawImage(tile[tileNum].image, x, y,null);
            col++;
            x += gp.tileSize;
            if (col == gp.maxScreenCol) {
                col = x = 0;
                row++;
                y += gp.tileSize;
            }
        }
    }

}
