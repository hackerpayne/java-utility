//package com.kyle.utility.image;
//
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//
///**
// * 图片水印服务
// */
//public class ImageWaterMarkUtil {
//
//    private String waterFile;
//    private int xPos;
//    private int yPos;
//    private int xInterval;
//    private int yInterval;
//    private float alpha;
//
//    public ImageWaterMarkUtil(String waterFile, int x, int y, int xInterval, int yInterval, float alpha) {
//        this.waterFile = waterFile;
//        this.xPos = x;
//        this.yPos = y;
//        this.xInterval = xInterval;
//        this.yInterval = yInterval;
//        this.alpha = alpha;
//    }
//
//    /**
//     * 图片加水印
//     * https://my.oschina.net/hansonwang99/blog/2876402
//     * imgFile 图像文件
//     * imageFileName 图像文件名
//     * uploadPath 服务器上上传文件的相对路径
//     * realUploadPath 服务器上上传文件的物理路径
//     */
//    public String watermarkAdd(File imgFile, String imageFileName, String uploadPath, String realUploadPath) {
//
//        String imgWithWatermarkFileName = "watermark_" + imageFileName;
//        OutputStream os = null;
//
//        try {
//            Image image = ImageIO.read(imgFile);
//
//            int width = image.getWidth(null);
//            int height = image.getHeight(null);
//
//            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  // ①
//            Graphics2D g = bufferedImage.createGraphics();  // ②
//            g.drawImage(image, 0, 0, width, height, null);  // ③
//
//            String logoPath = realUploadPath + "/" + this.waterFile;  // 水印图片地址
//            File logo = new File(logoPath);        // 读取水印图片
//            Image imageLogo = ImageIO.read(logo);
//
//            int markWidth = imageLogo.getWidth(null);    // 水印图片的宽度和高度
//            int markHeight = imageLogo.getHeight(null);
//
//            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, this.alpha));  // 设置水印透明度
//            g.rotate(Math.toRadians(-10), bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2);  // 设置水印图片的旋转度
//
//            int x = this.xPos;
//            int y = this.yPos;
//
//            int xInterval = this.xInterval;
//            int yInterval = this.yInterval;
//
//            double count = 1.5;
//            while (x < width * count) {  // 循环添加多个水印logo
//                y = -height / 2;
//                while (y < height * count) {
//                    g.drawImage(imageLogo, x, y, null);  // ④
//                    y += markHeight + yInterval;
//                }
//                x += markWidth + xInterval;
//            }
//
//            g.dispose();
//
//            os = new FileOutputStream(realUploadPath + "/" + imgWithWatermarkFileName);
//            JPEGImageEncoder en = JPEGCodec.createJPEGEncoder(os); // ⑤
//            en.encode(bufferedImage); // ⑥
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (os != null) {
//                try {
//                    os.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return uploadPath + "/" + imgWithWatermarkFileName;
//    }
//
//}
