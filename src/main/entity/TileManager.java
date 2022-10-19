package main.entity;

import main.system.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;

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

        setup(1, "Green", false);
        setup(2, "wall_stone", true);
        setup(3, "wall_32", true);
        setup(4, "black2", false);
        setup(5, "wall_plat", true);
    }

    public void setup(int index, String imageName, boolean collision) {

        try {
            tile[index] = new Tile();
            tile[index].image = ImageIO.read(getClass().getResourceAsStream("/images/" + imageName + ".png"));
            tile[index].collision = collision;
        } catch (Exception ignored) { }
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
                    col = 0; row++;
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
            col++; x += gp.tileSize;
            if (col == gp.maxScreenCol) {
                col = x = 0;
                row++;
                y += gp.tileSize;
            }
        }
    }

}
