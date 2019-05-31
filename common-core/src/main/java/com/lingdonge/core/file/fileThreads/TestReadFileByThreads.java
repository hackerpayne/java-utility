package com.lingdonge.core.file.fileThreads;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * 使用多线程读取超大文件。源码参考：
 * http://blog.csdn.net/hbyscl/article/details/22923683
 * 计算思路：
 * 1、计算出文件总大小
 * 2、分段处理,计算出每个线程读取文件的开始与结束位置
 * (文件大小/线程数)*N,N是指第几个线程,这样能得到每个线程在读该文件的大概起始位置
 * 使用"大概起始位置",作为读文件的开始偏移量(fileChannel.position("大概起始位置")),来读取该文件,直到读到第一个换行符,记录下这个换行符的位置,作为该线程的准确起
 * 始位置.同时它也是上一个线程的结束位置.最后一个线程的结束位置也直接设置为-1
 * 3、启动线程,每个线程从开始位置读取到结束位置为止
 */
public class TestReadFileByThreads {


    public static void main(String[] args) throws Exception {
        File file = new File("/Users/kyle/Downloads/日志分析/www.gebilaoshi.com.log");
        FileInputStream fis = null;

        try {
            ReadFile readFile = new ReadFile();
            fis = new FileInputStream(file);
            int available = fis.available();
            int maxThreadNum = 2;

            // 线程粗略开始位置
            int i = available / maxThreadNum;
            for (int j = 0; j < maxThreadNum; j++) {
                // 计算精确开始位置
                long startNum = j == 0 ? 0 : readFile.getStartNum(file, i * j);
                long endNum = j + 1 < maxThreadNum ? readFile.getStartNum(file, i * (j + 1)) : -2;

                System.out.println(MessageFormat.format("Read From {0} To {1}", startNum, endNum));

                // 具体监听实现,可以在这里打印文件的内容：精确到每一行
                ReaderFileListener listeners = new ReaderFileListener() {
                    @Override
                    public void output(List<String> stringList) throws Exception {
                        //System.out.println("[]" + stringList.size());

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