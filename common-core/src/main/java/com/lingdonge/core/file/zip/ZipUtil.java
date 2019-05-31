package com.lingdonge.core.file.zip;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件压缩处理类
 */
@Slf4j
public class ZipUtil {

    /**
     * @param destFile
     * @return
     */
    public static ZipOutputStream getZipOutputStream(String destFile) {
        try {
            return new ZipOutputStream(new FileOutputStream(destFile));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static ZipOutputStream getZipOutputStream(File destFile) {
        try {
            return new ZipOutputStream(new FileOutputStream(destFile));
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 压缩文件
     *
     * @param sourceFileName
     * @param destFile
     */
    public static void zipFile(String sourceFileName, String destFile) {

        ZipOutputStream out = null;
        BufferedOutputStream bos = null;
        try {
            //创建zip输出流
            out = new ZipOutputStream(new FileOutputStream(destFile));

            //创建缓冲输出流
            bos = new BufferedOutputStream(out);

            File sourceFile = new File(sourceFileName);

            //调用函数
            compress(out, bos, sourceFile, sourceFile.getName());

        } catch (Exception ex) {

        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(out);
        }

    }

    public static void compress(ZipOutputStream out, BufferedOutputStream bos, File sourceFile, String base) throws Exception {
        //如果路径为目录（文件夹）
        if (sourceFile.isDirectory()) {

            //取出文件夹中的文件（或子文件夹）
            File[] flist = sourceFile.listFiles();

            if (flist.length == 0)//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
            {
                System.out.println(base + "/");
                out.putNextEntry(new ZipEntry(base + "/"));
            } else//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
            {
                for (int i = 0; i < flist.length; i++) {
                    compress(out, bos, flist[i], base + "/" + flist[i].getName());
                }
            }
        } else//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
        {
            out.putNextEntry(new ZipEntry(base));
            FileInputStream fos = new FileInputStream(sourceFile);
            BufferedInputStream bis = new BufferedInputStream(fos);

            int tag;
            System.out.println(base);
            //将源文件写入到zip文件中
            while ((tag = bis.read()) != -1) {
                bos.write(tag);
            }
            bis.close();
            fos.close();

        }
    }


}
