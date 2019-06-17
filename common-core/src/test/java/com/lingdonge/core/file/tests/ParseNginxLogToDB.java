package com.lingdonge.core.file.tests;

import cn.hutool.core.util.StrUtil;
import com.lingdonge.core.dates.DateUtil;
import com.lingdonge.core.file.FileUtil;
import com.lingdonge.core.util.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kyle on 17/4/13.
 */
public class ParseNginxLogToDB {

    public static Logger logger = LoggerFactory.getLogger(ParseNginxLogToDB.class.getName());


    final static String regexLog = "(\\d{1,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2})\\s(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})\\s(.*?)\\s(.*?)\\s(.*?)\\s(.*?)\\s(.*?)\\s(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})\\s(.*?)\\s(.*?)\\s(.*?)\\s(.*?)\\s(\\d{1,5})";

    final static String regexLog2 = "(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})\\s-\\s(.*?)\\s\\[(.*?)\\]\\s\"(.*?)\\\"\\s(.*?)\\s\"(.*?)\"\\s\"(.*?)\"";

    final static String regexLog3 = "(.*?)\\s(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})\\s(.*?)\\s(.*?)\\s\\[(.*?)\\]\\s\"(.*?)\"\\s(\\d{1,5})\\s(.*?)\\s\"(.*?)\"\\s\"(.*?)\"";

    final static String regexLog4 = "(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})\\s(.*?)\\s(.*?)\\s\\[(.*?)\\]\\s\\\"(.*?)\\\"\\s(.*?)\\s(.*?)\\s(.*?)\\s\\\"(.*?)\\\"\\s(.*?)\\s\\\"(.*?)\\\"\\s";

    public static void main(String[] args) throws Exception {

        logger.info("当前时间：" + DateUtil.getNowTime());

//        File file = new File("/Users/kyle/Downloads/四川航空日志/4/apachelog/accesslog.201706280000");

//        DBSaverTemplate.insertUA("180.97.35.206", "");

//        DBManager.monitorThread();//开启监控进程
//
//        DBManager.startInsertDBThread();//保存数据库队列

        flagStatus = false;

        List<File> files = FileUtil.loopFiles(new File("/Users/kyle/Downloads/四川航空日志/4/"));
        int pos = 0;
        for (File file : files) {
            pos++;
            if (file.getAbsolutePath().contains("DS_Store")) continue;
            logger.info(StrUtil.format("读取文件【{}/{}】：{}", pos, files.size(), file.getAbsolutePath()));

            try {
                process(file);
            } catch (Exception e) {
                logger.info(e.getMessage());
                process(file);//重试
            }
        }

        flagStatus = true;//标记已经处理完

    }

    public static boolean flagStatus = false;

    private static Thread th;


    /**
     * 遍历文件进行读取操作
     *
     * @param file
     */
    public static void process(File file) {
        logger.info("读取文件：" + file.getPath());

        final Pattern pattern = Pattern.compile(regexLog4);

        LineIterator it = null;
        try {
            it = FileUtils.lineIterator(file, "UTF-8");

            Integer pos = 0;

            String userAgent;
            String ipAddr;

            String line = "";
            Matcher matcher;

            while (it.hasNext()) {
                pos++;

//                logger.info("正在处理第" + pos + "行");

                // 只读取前10行。
//                if (pos > 10) break;

                line = it.nextLine();
                if (StringUtils.isEmpty(line)) continue;

                if (line.startsWith("#")) continue;//带#开头的行，是注释，跳开

                // 方法一：通过分割字符串进行匹配
//                List<String> split = Splitter.on("\u0001").splitToList(line);
//
//                String ipAddr = split.get(0);
//                String userAgent = split.get(7);

                // 方法二：通过正则进行匹配
                matcher = pattern.matcher(line);

                if (!matcher.find()) {
                    logger.info(StrUtil.format("匹配文件{}，第{}行，失败", file.getPath(), pos));
                    ipAddr = "";
                    userAgent = "";
                } else {

                    userAgent = matcher.group(11);
                    ipAddr = matcher.group(1);
                }

//                //往里面扔就行了，会自动去重
//                if (StringUtils.isNotEmpty(ipAddr))
//                    DBManager.addIPAddress(ipAddr);
//
//                if (StringUtils.isNotEmpty(userAgent))
//                    DBManager.addUserAgent(userAgent);

//                logger.info(StringUtils.format("读取到IP地址：{}，UA：{}", ipAddr, userAgent));

            }

            logger.info(StrUtil.format("读取文件：{}结束，共计{}行", file, pos));
        } catch (IOException e) {
            logger.error("读取文件：" + file.getPath() + "，发生异常", e);
        } finally {
            LineIterator.closeQuietly(it);
        }
    }

}
