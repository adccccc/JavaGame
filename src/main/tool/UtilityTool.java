package main.tool;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class UtilityTool {

    // 缩放
    public static BufferedImage scaleImage(BufferedImage original, double scale) {

        double w = original.getHeight() * scale, h = original.getHeight() * scale;
        BufferedImage scaleImage = new BufferedImage((int)w, (int)h, original.getType());
        Graphics2D g2 = scaleImage.createGraphics();

        g2.drawImage(original, 0, 0, (int)w, (int)h, null);
        return scaleImage;
    }

    // 水平翻转图片
    public static BufferedImage horizontalFlipImage(BufferedImage img) {

        AffineTransform transform = new AffineTransform(-1,0,0,1,img.getWidth()-1,0);//水平翻转
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(img, null);
    }

    // 旋转
    public static BufferedImage rotate(BufferedImage original, int rotateAngle) {

        int srcWidth = original.getWidth(), srcHeight = original.getHeight();
        // 旋转
        Rectangle rectDes = CalcRotatedSize(new Rectangle(new Dimension(srcWidth, srcHeight)), rotateAngle);
        BufferedImage result = new BufferedImage(rectDes.width, rectDes.height, original.getType());
        Graphics2D g2 = result.createGraphics();
        g2.translate((rectDes.width - srcWidth) / 2,
                (rectDes.height - srcHeight) / 2);
        g2.rotate(Math.toRadians(rotateAngle), srcWidth / 2.0, srcHeight / 2.0);
        g2.drawImage(original, null, null);

        return result;
    }

    private static Rectangle CalcRotatedSize(Rectangle src, int angel) {

        if (angel >= 90) {
            if(angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }

        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angleDeltaWidth = Math.atan((double) src.height / src.width);
        double angleDeltaHeight = Math.atan((double) src.width / src.height);

        int lenDeltaWidth = (int) (len * Math.cos(Math.PI - angel_alpha
                - angleDeltaWidth));
        int lenDeltaHeight = (int) (len * Math.cos(Math.PI - angel_alpha
                - angleDeltaHeight));
        int des_width = src.width + lenDeltaWidth * 2;
        int des_height = src.height + lenDeltaHeight * 2;
        return new java.awt.Rectangle(new Dimension(des_width, des_height));
    }

}
