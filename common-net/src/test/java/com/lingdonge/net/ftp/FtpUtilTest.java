package com.lingdonge.net.ftp;

import org.junit.Test;

public class FtpUtilTest {


    /**
     * 两个功能其中一个使用的话另一个需要注释
     */
    @Test
    public void test() {
        //上传测试--------------------------------------
        /*FileInputStream in;
        try {
            in=new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\json.png"));
            FtpUtil ftp=new FtpUtil();
            boolean flag=ftp.fileUpload("/2015/06/04", "va.jpg", in);
            System.out.println(flag);
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
        }*/
        //下载测试--------------------------------------
        /*String filename="/2015/06/04/aa.jpg";
        String localPath="F:\\";
        FtpUtil ftp=new FtpUtil();
        ftp.downloadFile(filename, localPath);*/
        //删除测试--------------------------------------
        FtpUtil ftputil = new FtpUtil();
        boolean flag = ftputil.deleteFile("/2015/06/04/va.jpg");
        System.out.println(flag);
    }


}