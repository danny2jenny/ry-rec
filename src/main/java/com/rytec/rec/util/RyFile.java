package com.rytec.rec.util;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by danny on 17-5-9.
 */
public class RyFile {

    static private int thumbWidth = 128;

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    public static void makeThumb(String infile, String extName) {
        try {

            // 大图文件
            File fiBig = new File(infile);
            // 将要转换出的小图文件
            File foSmall = new File(fiBig.getParent() + "/_" + fiBig.getName());

            AffineTransform transform = new AffineTransform();

            //读取图片
            BufferedImage bis = ImageIO.read(fiBig);
            //获得图片原来的高宽
            int w = bis.getWidth();
            int h = bis.getHeight();

            double scale = (double) w / h;

            int newWidth = thumbWidth;
            int newHeight = (newWidth * h) / w;

            if (newHeight > thumbWidth) {
                newHeight = thumbWidth;
                newWidth = (newHeight * w) / h;
            }

            double sx = (double) newWidth / w;
            double sy = (double) newHeight / h;

            transform.setToScale(sx, sy);

            AffineTransformOp ato = new AffineTransformOp(transform, null);
            BufferedImage bid = new BufferedImage(newWidth, newHeight,
                    BufferedImage.TYPE_3BYTE_BGR);
            ato.filter(bis, bid);

            ImageIO.write(bid, extName, foSmall);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
