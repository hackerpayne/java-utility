package com.lindonge.core.file.tests;

import com.google.common.base.Splitter;
import com.lindonge.core.file.fileThreads.ReadFile;
import com.lindonge.core.file.fileThreads.ReadFileThread;
import com.lindonge.core.file.fileThreads.ReaderFileListener;
import com.lindonge.core.dates.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * 解析字符串分割类的日志 到数据库
 * Created by kyle on 17/4/13.
 */
public class ParseSeperateLogToDB {

    public static Logger logger = LoggerFactory.getLogger(ParseSeperateLogToDB.class);

    public static void main(String[] args) throws Exception {

        logger.info("当前时间：" + DateUtil.getNowTime());

//        DBManager.monitorThread();//开启监控进程

        File file = new File("/Users/kyle/Downloads/日志分析/12服务器原始日志/public_2015-10-22_00-01.log");
        FileInputStream fis = null;

        try {
            ReadFile readFile = new ReadFile();
            fis = new FileInputStream(file);
            int available = fis.available();
            int maxThreadNum = 50;

            // 线程粗略开始位置
            int i = available / maxThreadNum;
            for (int j = 0; j < maxThreadNum; j++) {
                // 计算精确开始位置
                long startNum = j == 0 ? 0 : readFile.getStartNum(file, i * j);
                long endNum = j + 1 < maxThreadNum ? readFile.getStartNum(file, i * (j + 1)) : -2;

                System.out.println(MessageFormat.format("Read From {0} To {1}", startNum, endNum));
                // 具体监听实现
                ReaderFileListener listeners = new ReaderFileListener() {
                    @Override
                    public void output(List<String> stringList) throws Exception {

                        String ua = "";
                        String ip = "";

                        for (String line : stringList) {

                            try {
                                List<String> split = Splitter.on("\u0001").splitToList(line);

                                ip = split.get(0);
                                ua = split.get(7);

//                                DBManager.addIPAddress(ip);
//                                DBManager.addUserAgent(ua);

//                                DBSaverTemplate.insertUA(ip, ua);

//                                logger.info(ip + "=====" + ua);


                            } catch (Exception ex) {
                                logger.error(ex.getMessage());
                            }

                        }
                    }
                };

                listeners.setEncode("utf-8");

                new ReadFileThread(listeners, startNum, endNum, file.getPath()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
