package com.lingdonge.http.httpclient.downloader;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 多线程下载断点续传
 * 代码来源：https://blog.csdn.net/u014248897/article/details/76039725
 */
public class MulitDownload {
    //设置线程数量
    static int thradCount = 3;
    //设置线程数量读取
    static int finishedThread = 0;

    //1.设置获取下载地址Path
    static String path = "http://192.168.108.2:8080/WPS.exe";


    public static void main(String[] args) {
        //2.设置URL侦听
        try {
            URL url = new URL(path);
            //3.设置建立网络连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //4.设置请求方式
            conn.setRequestMethod("GET");
            //设置响应时长和返回时长
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            //5.建立网络连接
            conn.connect();
            //6.判断响应码是否为200
            if (conn.getResponseCode() == 200) {
                //7.获取下载文件大小
                int length = conn.getContentLength();
                //设置保存位置
                File file = new File(getFileName(path));
                //8.设置缓存
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                //设置临时文件大小
                raf.setLength(length);
                raf.close();
                //9.设置每个线程读取多少
                int size = length / thradCount;
                for (int i = 0; i < thradCount; i++) {
                    int startIndex = i * size;
                    int endIndex = (i + 1) * size - 1;
                    if (i == thradCount - 1) {
                        endIndex = length - 1;
                    }
                    System.out.println("线程数" + i + "开始区间" + startIndex + "--" + endIndex);
                    new DownloadThread(startIndex, endIndex, i).start();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getFileName(String path) {
        int index = path.lastIndexOf("/");
        return path.substring(index + 1);
    }
}