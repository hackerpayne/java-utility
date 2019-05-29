package com.lindonge.core.file.tests;

import com.lindonge.core.util.StringUtils;
import com.lindonge.core.util.Utils;
import com.lindonge.core.dates.DateUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * 合并多个日志文件
 * Created by kyle on 2017/6/13.
 */
public class CombileLogFiles {

    public static Logger logger = LoggerFactory.getLogger(CombileLogFiles.class.getName());

    public static void main(String[] args) {

        File dirFiles = new File("/Users/kyle/Downloads/日志分析");

        saveFile = new File("/Users/kyle/Downloads/日志分析/multithreading.txt");

        // 遍历目录下面的文件
        Collection<File> listFiles = FileUtils.listFiles(dirFiles, new String[]{"log"}, false);

        for (File file : listFiles) {
//            System.out.println(file.getPath());
            processFile(file);
        }
    }

    private static File saveFile;

    /**
     * 读取和处理文件内容
     *
     * @param file
     * @throws Exception
     */
    public static void processFile(File file) {
        logger.info("当前时间：" + DateUtil.getNowTime());

        logger.info("读取文件：" + file.getPath());

        LineIterator it = null;
        try {
            it = FileUtils.lineIterator(file, "UTF-8");
            while (it.hasNext()) {
                String line = it.nextLine();
                if (StringUtils.isEmpty(line)) continue;

                if (line.startsWith("#")) continue;//带#开头的行，是注释，跳开

                FileUtils.write(saveFile, line.trim() + Utils.LineSeparator, "utf-8", true);

            }
        } catch (IOException e) {
            logger.error("读取文件：" + file.getPath() + "，发生异常", e);
        } finally {
            LineIterator.closeQuietly(it);
        }

    }
}
