package com.lingdonge.core.file;

import cn.hutool.core.io.IORuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 目录操作辅助类
 */
@Slf4j
public class DirUtil {

    /**
     * 判断是否为目录，如果path为null，则返回false
     *
     * @param path 文件路径
     * @return 如果为目录true
     */
    public static boolean isDirectory(String path) {
        return (path != null) && FileUtil.file(path).isDirectory();
    }

    /**
     * 判断是否为目录，如果file为null，则返回false
     *
     * @param file 文件
     * @return 如果为目录true
     */
    public static boolean isDirectory(File file) {
        return (file != null) && file.isDirectory();
    }

    /**
     * 创建目录
     *
     * @param path 要创建的目录路径
     */
    public static void createDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     * 删除目录
     *
     * @param path
     */
    public static void delDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] tmp = dir.listFiles();
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].isDirectory()) {
                    delDir(path + "/" + tmp[i].getName());
                } else {
                    tmp[i].delete();
                }
            }
            dir.delete();
        }
    }


    /**
     * 目录是否为空
     *
     * @param dirPath 目录
     * @return 是否为空
     * @throws IORuntimeException IOException
     */
    public static boolean isDirEmpty(Path dirPath) {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dirPath)) {
            return !dirStream.iterator().hasNext();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 目录是否为空
     *
     * @param dir 目录
     * @return 是否为空
     */
    public static boolean isDirEmpty(File dir) {
        return isDirEmpty(dir.toPath());
    }

    /**
     * 获取目录下面的子目录，只能获取一层目录
     *
     * @param dir
     * @return
     */
    public static List<File> getDirDirectory(String dir) {
        File[] fileLists = FileUtil.file(dir).listFiles(); // 如果是目录，获取该目录下的内容集合

        List<File> listDirectory = new ArrayList<>();
        for (int i = 0; i < fileLists.length; i++) { // 循环遍历这个集合内容
//            System.out.println(fileLists[i].getName());    //输出元素名称
            if (fileLists[i].isDirectory()) {    //判断元素是不是一个目录
                listDirectory.add(fileLists[i]);    //如果是目录，继续调用本方法来输出其子目录
            }
        }
        return listDirectory;
    }
}
