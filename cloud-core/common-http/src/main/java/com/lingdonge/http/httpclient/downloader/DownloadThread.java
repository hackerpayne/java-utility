package com.lingdonge.http.httpclient.downloader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadThread extends Thread {
    int startIndex;
    int endIndex;
    int threadId;

    public DownloadThread(int startIndex, int endIndex, int threadId) {
        super();
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        super.run();
        URL url;
        try {
            //设置断点续传ID
            File progressFile = new File(threadId + ".txt");
            if (progressFile.exists()) {
                FileInputStream fis = new FileInputStream(progressFile);
                BufferedReader bf = new BufferedReader(new InputStreamReader(fis));
                startIndex += Integer.valueOf(bf.readLine());
            }
            System.out.println("线程" + threadId + "开始节点" + startIndex + "后续" + endIndex);
            url = new URL(MulitDownload.path);

            //3.设置建立网络连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//4.设置请求方式
            conn.setRequestMethod("GET");
//设置响应时长和返回时长
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
//拿到节点
            conn.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
//判断当前节点是否为206
            if (conn.getResponseCode() == 206) {
//获取输入流
                InputStream is = conn.getInputStream();
                byte[] b = new byte[1024];
                int len = 0;
                int total = 0;
                File file = new File(MulitDownload.getFileName(MulitDownload.path));
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
//指定从哪个节点开始
                raf.seek(startIndex);
                while ((len = is.read(b)) != -1) {
                    raf.write(b, 0, len);
                    total += len;
                    System.out.println("当前线程" + threadId + "下载" + total);


//设置缓存读取
                    RandomAccessFile progressRaf = new RandomAccessFile(progressFile, "rwd");
//设置写入文本信息
                    progressRaf.write((total + "").getBytes());
                    progressRaf.close();

                }
                raf.close();
                //设置读取线程 数量
                MulitDownload.finishedThread++;
//同步语句
                synchronized (MulitDownload.path) {
                    if (MulitDownload.finishedThread == MulitDownload.thradCount) {
                        for (int i = 0; i < MulitDownload.thradCount; i++) {
                            File f = new File(i + ".txt");
                            f.delete();
                            System.out.println("打印当前" + i);
                        }
//设置判断为0
                        MulitDownload.finishedThread = 0;
                    }

                }

            }
        } catch (Exception e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

}
