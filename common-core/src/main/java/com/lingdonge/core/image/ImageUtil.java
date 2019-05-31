package com.lingdonge.core.image;

import com.lingdonge.core.encode.Base64Util;
import com.lingdonge.core.encode.CharsetUtil;
import com.lingdonge.core.exceptions.IORuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class ImageUtil {

    /**
     * Image转换为Base64格式
     *
     * @param image
     * @return
     * @throws IOException
     */
    public static String imageToBase64Str(BufferedImage image) throws IOException {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", bos);
            byte[] imageBytes = bos.toByteArray();
            imageString = Base64.encodeBase64String(imageBytes);
            bos.close();
        } catch (IOException e) {
            throw new IOException(e);
        }
        return imageString;
    }


    /**
     * 将Base64编码的图像信息转为 {@link BufferedImage}
     *
     * @param base64 图像的Base64表示
     * @return {@link BufferedImage}
     * @throws IORuntimeException IO异常
     */
    public static BufferedImage toImage(String base64) throws IORuntimeException {
        byte[] decode = Base64Util.decodeToBytes(base64, CharsetUtil.CHARSET_UTF_8);
        return toImage(decode);
    }

    /**
     * 将Base64编码的图像信息转为 {@link BufferedImage}
     *
     * @param imageBytes 图像bytes
     * @return {@link BufferedImage}
     * @throws IORuntimeException IO异常
     */
    public static BufferedImage toImage(byte[] imageBytes) throws IORuntimeException {
        try {
            return ImageIO.read(new ByteArrayInputStream(imageBytes));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

}
