package main.entity;

import main.system.GamePanel;
import main.tool.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {

    public GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];

    public TileManager(GamePanel gp) {

        this.gp = gp;
        tile = new Tile[10];
        mapTileNum = new int[gp.maxScreenCol][gp.maxScreenRow];
        getTileImage();
    }

    public void getTileImage() {

        setup(1, "Green", false, false);
        setup(2, "wall", true, false);
        setup(3, "platform", false, true);
    }

    public void setup(int index, String imageName, boolean collision, boolean platform) {

        try {
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(getClass().getResourceAsStream("/images/" + imageName + ".png"));
            tile[index].image = UtilityTool.scaleImage(tile[index].image, gp.tileSize, gp.tileSize);
            tile[index].collision = collision;
            tile[index].platform = platform;
        } catch (Exception e) {}
    }

    public void loadMap(int level) {

        try {
            InputStream in = getClass().getResourceAsStream("/maps/tile" + level + ".csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            int col = 0, row = 0;
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
