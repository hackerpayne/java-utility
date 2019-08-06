package com.lingdonge.core.file.tests;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lingdonge.core.file.fileThreads.ReadFile;
import com.lingdonge.core.file.fileThreads.ReadFileThread;
import com.lingdonge.core.file.fileThreads.ReaderFileListener;
import com.lingdonge.core.dates.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * Created by kyle on 17/4/13.
 */
public class ParseJsonLogToDB {

    public static Logger logger = LoggerFactory.getLogger(ParseJsonLogToDB.class.getName());

    public static void main(String[] args) throws Exception {


        logger.info("当前时间：" + DateUtil.getNowTime());

        File file = new File("/Users/kyle/Downloads/日志分析/analysis.bingyes.com.access_20160703.log");
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

                logger.info(MessageFormat.format("Read From {0} To {1}", startNum, endNum));

                // 具体监听实现
                ReaderFileListener listeners = new ReaderFileListener() {
                    @Override
                    public void output(List<String> stringList) throws Exception {

                        String ua = "";
                        String ip = "";
                        for (String line : stringList) {

                            try {
                                JSONObject object = JSON.parseObject(line);

                                ua = object.getString("ua");
                                ip = object.getString("ip");

//                                DBSaverTemplate.insertUA(ip, ua);
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
