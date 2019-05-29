package com.lingdonge.http.faker;

import com.lindonge.core.file.FileUtil;
import com.lindonge.core.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * 生成手机号码段
 */
@Slf4j
public class MobileFacker {

    /**
     * 生成指定长度的号码，前面自动补0
     *
     * @param length
     * @return
     */
//    public List<String> generateNumber(String length) {
//
//    }


    /**
     * 不够位数的在前面补0，保留code的长度位数字
     *
     * @param code
     * @return
     */
    private String autoGenericCode(String code) {
        // 保留code的位数
        return String.format("%0" + code.length() + "d", Integer.parseInt(code) + 1);
    }

    /**
     * 不够位数的在前面补0，保留num的长度位数字
     *
     * @param code
     * @param num
     * @return
     */
    public static String autoGenericCode(String code, int num) {
        // 保留num的位数
        // 0 代表前面补充0
        // num 代表长度为4
        // d 代表参数为正数型
        return String.format("%0" + num + "d", Integer.parseInt(code) + 1);
    }

    /**
     * 使用数字进行补齐
     *
     * @param code
     * @param num
     * @return
     */
    public static String autoGenericCode(Integer code, int num) {
        // 保留num的位数
        // 0 代表前面补充0
        // num 代表长度为4
        // d 代表参数为正数型
        return String.format("%0" + num + "d", code + 1);
    }

    public static void main(String[] args) {

        String filePath = "/Users/kyle/项目Project/小向-企业信息抓取项目/号段生成器/安徽-合肥-电信-594.txt";

        String savePath = "/Users/kyle/Downloads/号段生成结果/";

        LineIterator it = null;
        try {
            File saveFile;

            it = FileUtils.lineIterator(new File(filePath), "UTF-8");
            while (it.hasNext()) {
                String line = it.nextLine();
                if (StringUtils.isEmpty(line)) {
                    continue;
                }

                saveFile = FileUtil.getFile(savePath, "安徽-合肥-电信-" + line + ".txt");
                for (int i = 0; i < 9999; i++) {
                    FileUtils.write(saveFile, line.trim() + autoGenericCode(i, 4) + Utils.LineSeparator, "utf-8", true);

                }

//                break;

            }
        } catch (IOException e) {
            log.error("读取文件：" + filePath + "，发生异常", e);
        } finally {
            IOUtils.closeQuietly(it);
        }

        System.out.println("处理完成");

//        for (int i = 0; i < 1000; i++) {
//            System.out.println(autoGenericCode(i, 3));
//        }
    }
}
