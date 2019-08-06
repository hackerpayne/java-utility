package com.lingdonge.http.webmagic.webmagic.ocr;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.UUID;


/**
 * 使用OCR进行识别的类
 * 调用tesseract进程，返回识别结果。
 */
public class TesseractOcrUtil {
    private static final Logger log = LoggerFactory
            .getLogger(TesseractOcrUtil.class);
    private static final String tessPath = "";
    private static final String basePath;

    static {
//        tessPath = FileUtils.getCommonProp("tesseract.path");
        basePath = new File(tessPath).getParentFile().getAbsolutePath();
    }

    public static String getByLangNum(String imagePath) {
        return get(imagePath, "num");
    }

    public static String getByLangChi(String imagePath) {
        return get(imagePath, "chi_sim");
    }

    public static String getByLangEng(String imagePath) {
        return get(imagePath, "eng");
    }

    public static String get(String imagePath, String lang) {
        String outName = UUID.randomUUID().toString();
        String outPath = basePath + File.separator
                + outName + ".txt";
//        String cmd = tessPath + " " + imagePath + " " + outName + " -l " + lang;
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(new File(basePath));

        pb.command(tessPath, imagePath, outName, "-l", lang);

        pb.redirectErrorStream(true);

        Process process = null;
        String errormsg = "";
        String res = null;
        try {
            process = pb.start();
            // tesseract.exe 1.jpg 1 -l chi_sim
            int excode = process.waitFor();

            if (excode == 0) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        new FileInputStream(outPath), "UTF-8"));
                res = in.readLine();
                IOUtils.closeQuietly(in);
            } else {
                switch (excode) {
                    case 1:
                        errormsg = "Errors accessing files.There may be spaces in your image's filename.";
                        break;
                    case 29:
                        errormsg = "Cannot recongnize the image or its selected region.";
                        break;
                    case 31:
                        errormsg = "Unsupported image format.";
                        break;
                    default:
                        errormsg = "Errors occurred.";
                }
                log.error("when ocr picture " + imagePath
                        + " an error occured. " + errormsg);
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.warn("orc process occurs an io error", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.warn("orc process was interrupt unexpected!", e);
        } finally {
            FileUtils.deleteQuietly(new File(imagePath));
            FileUtils.deleteQuietly(new File(outPath));
        }
        if (res != null) {
            res = res.trim();
        }
        return res;
    }
}