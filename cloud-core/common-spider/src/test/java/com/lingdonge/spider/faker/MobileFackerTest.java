package com.lingdonge.spider.faker;

import cn.hutool.core.io.IoUtil;
import com.lingdonge.core.file.FileUtil;
import com.lingdonge.core.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@Slf4j
public class MobileFackerTest {

    @Test
    public void test() {

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

                saveFile = FileUtil.file(savePath, "安徽-合肥-电信-" + line + ".txt");
                for (int i = 0; i < 9999; i++) {
                    FileUtils.write(saveFile, line.trim() + MobileFacker.autoGenericCode(i, 4) + Utils.LineSeparator, "utf-8", true);

                }

//                break;

            }
        } catch (IOException e) {
            log.error("读取文件：" + filePath + "，发生异常", e);
        } finally {
            IoUtil.close(it);
        }

        System.out.println("处理完成");

//        for (int i = 0; i < 1000; i++) {
//            System.out.println(autoGenericCode(i, 3));
//        }
    }

}