package main.system;

import java.awt.image.BufferedImage;

// 封装动态图效果
public class DynamicImage {

    private BufferedImage[] images;
    private final int frame; // 每张图片的帧数
    private int counter; // 内部计数器

    public DynamicImage(int frame, BufferedImage... images) {

        this.frame = frame;
        this.images = images.clone();
        this.counter = 0;
    }

    public DynamicImage(BufferedImage... images) {this(1, images);}

    public BufferedImage getImg() {

        if (++counter >= frame * images.length)
            counter = 0;
        return images[counter / frame];
    }
}