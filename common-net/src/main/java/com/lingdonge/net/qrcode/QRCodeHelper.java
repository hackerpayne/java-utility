package com.lingdonge.net.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Zxing二维码生成辅助类
 * Created by kyle on 17/4/24.
 */
@Slf4j
public class QRCodeHelper {

    /**
     * 生成网址二维码，保存到指定的文件内
     *
     * @param message  需要生成的消息
     * @param saveFile 保存文件名
     */
    public void genQrcode(String message, String saveFile) {
        //输出目标文件
        File file = new File(saveFile);
        if (!file.exists()) {
            try {
                file.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                log.error("genQrcode文件不存在", e);
            }
        }

        //设置参数，输出文件
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(message, BarcodeFormat.QR_CODE, 300, 300, hints);// 生成矩阵
            MatrixToImageWriter.writeToFile(bitMatrix, "png", file);// 输出图像
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
