package com.lingdonge.core.file;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FileUtilTest {

    @Test
    public void testFile() {

        System.out.println("multithreading file helper");

        System.out.println(FileUtils.getFile("data", "src", "ok.jpg"));

//
//
//        String filePath = "/Users/kyle/Downloads/日志分析/www.gebilaoshi.com.log";
//        RandomAccessFile br = new RandomAccessFile(filePath, "r");//这里rw看你了。要是之都就只写r
//        String str = null;
//        int i = 0;
//        while ((str = br.readLine()) != null) {
//            i++;
//
//            System.out.println(str);
//            if (i > 10)
//                break;
////            if(i%99==0){//假设读取100行
////                System.out.println("读取到第【"+i+"】行");
////            }
//        }
//        br.close();
//
//        System.out.println("读取完成！");
    }
}