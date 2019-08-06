package com.lingdonge.core.file;

import cn.hutool.core.io.IoUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.lingdonge.core.collection.CollectionUtil;
import com.lingdonge.core.util.StringUtils;
import com.lingdonge.core.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import java.io.*;
import java.util.List;

/**
 * Created by Kyle on 16/6/7.
 */
@Slf4j
public class FileUtil extends cn.hutool.core.io.FileUtil {


    /**
     * 冗余的获取当前文件的路径
     * 从当前目录，config等目录，统一进行获取
     *
     * @param filePath
     * @return
     */
    public static File getFilePath(String filePath) {

        File destPath = FileUtils.getFile(filePath);
        if (destPath.exists()) {
            return destPath;
        }

        destPath = FileUtils.getFile(Utils.CurrentDir, filePath);
        if (destPath.exists()) {
            return destPath;
        }

        destPath = FileUtils.getFile(Utils.CurrentDir, "config", filePath);
        if (destPath.exists()) {
            return destPath;
        }

        return null;
    }

    /**
     * 递归遍历目录以及子目录中的所有文件
     *
     * @param file 当前遍历文件
     * @return 文件列表
     */
    public static List<File> loopFiles(File file) {
        return loopFiles(file, "");
    }

    /**
     * 返回指定扩展名的文件列表
     *
     * @param file
     * @param filter
     * @return
     */
    public static List<File> loopFiles(File file, String filter) {
        return cn.hutool.core.io.FileUtil.loopFiles(file, new FileFilter() {

            @Override
            public boolean accept(File file) {
                final String path = file.getPath();
                if (StringUtils.isEmpty(filter)) {
                    return true;
                }

                if (StringUtils.isNotEmpty(path) && path.toLowerCase().endsWith(filter)) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 把文件中的每一行，换正则切割 到指定的列表里面，自动去除空格和空字符
     * 小文件可以这样操作
     *
     * @param file
     * @param splitPattern
     * @return
     */
    public static List<List<String>> readAndSplitToList(File file, String splitPattern) {

        List<List<String>> listResults = Lists.newArrayList();

        LineIterator it = null;
        try {
            it = FileUtils.lineIterator(file, "UTF-8");

            List<String> tempSplits;
            while (it.hasNext()) {
                String line = it.nextLine();
                if (StringUtils.isEmpty(line)) {
                    continue;
                }

                tempSplits = Splitter.onPattern(splitPattern).omitEmptyStrings().trimResults().splitToList(line);

                listResults.add(tempSplits);
            }
        } catch (IOException e) {
            log.error("读取文件：" + file.getPath() + "，发生异常", e);
        } finally {
            IoUtil.close(it);
        }
        return listResults;
    }

    /**
     * 把List的数据原样存入文件中去
     *
     * @param file
     * @param listLines
     * @param joinStr
     */
    public static void writeListToFile(File file, List<List<String>> listLines, String joinStr) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            for (int i = 0; i < listLines.size(); i++) {
                bw.write(Joiner.on(joinStr).join(listLines.get(i)));
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(fw);
        }
    }

    /**
     * 获取相对的文件路径，如果有此文件，直接返回，否则在根目录下面进行寻找
     *
     * @param filePath
     * @return
     */
    public static File getRelativeFile(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            return file;
        }

        file = cn.hutool.core.io.FileUtil.file(Utils.CurrentDir, filePath);

        return file;
    }

    /**
     * 读取并获取文件中的随机一行的结果
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String getRandOneline(String filePath) throws IOException {
        List<String> fileLines = FileUtils.readLines(new File(filePath), "utf-8");
        return CollectionUtil.getRandomItem(fileLines);
    }

    /**
     * 创建空文件
     *
     * @param path
     * @param filename
     * @throws IOException
     */
    public static void createEmptyFile(String path, String filename) throws IOException {
        File file = new File(path + "/" + filename);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    /**
     * 利用FileInputStream读取文件
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static String readFileByFileInputStream(String path) throws IOException {
        File file = new File(path);
        if (!file.exists() || file.isDirectory())
            throw new FileNotFoundException();
        FileInputStream fis = new FileInputStream(file);
        byte[] buf = new byte[1024];
        StringBuffer sb = new StringBuffer();
        while ((fis.read(buf)) != -1) {
            sb.append(new String(buf));
            buf = new byte[1024];//重新生成，避免和上次读取的数据重复
        }
        return sb.toString();
    }

    /**
     * 在IO操作，利用BufferedReader和BufferedWriter效率会更高一点
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static String readFileByBufferReader(String path) throws IOException {
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        String temp = null;
        StringBuffer sb = new StringBuffer();
        temp = br.readLine();
        while (temp != null) {
            sb.append(temp + " ");
            temp = br.readLine();
        }

        return sb.toString();
    }

    /**
     * StringBuffer写文件
     * 可以设定使用何种编码，有效解决中文问题。
     *
     * @throws IOException
     */
    public static void writeFileByStringBuffer(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(file, true);
        for (int i = 0; i < 10000; i++) {
            StringBuffer sb = new StringBuffer();
            sb.append("这是第" + i + "行:前面介绍的各种方法都不关用,为什么总是奇怪的问题 ");
            out.write(sb.toString().getBytes("utf-8"));
        }
        out.close();
    }

    /**
     * @param filePath
     * @param lines
     */
    public static void writeFileByBufferOutPutStream(String filePath, List<String> lines) {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try {
            fos = new FileOutputStream(filePath);
            bos = new BufferedOutputStream(fos);

            for (int i = 0; i < lines.size(); i++) {
                bos.write(lines.get(i).getBytes("UTF-8"));
            }
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            IOUtils.closeQuietly(bos);
        }
    }

    public static void writeFileByBufferWriter(String filePath, List<String> lines) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(filePath);
            bw = new BufferedWriter(fw);
            for (int i = 0; i < lines.size(); i++) {
                bw.write(lines.get(i));
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fw);
        }
    }

    /**
     * 复制文件
     *
     * @param src
     * @param desc
     * @throws IOException
     */
    public static void copyFile(String src, String desc) throws IOException {
        FileInputStream in = new FileInputStream(src);
        File file = new File(desc);
        if (!file.exists()) file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        int c;
        byte buffer[] = new byte[1024];
        while ((c = in.read(buffer)) != -1) {
            for (int i = 0; i < c; i++) {
                out.write(buffer[i]);
            }
        }
        in.close();
        out.close();
    }

    /**
     * 超大文件读写
     *
     * @param inputFile
     * @param outputFile
     */
    public static void largeFileIO(String inputFile, String outputFile) {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(inputFile)));
            BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10 * 1024 * 1024);//10M缓存
            FileWriter fw = new FileWriter(outputFile);
            while (in.ready()) {
                String line = in.readLine();
                fw.append(line + " ");
            }
            in.close();
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 重命名文件
     *
     * @param oldname
     * @param newname
     * @param deleteNew 如果新文件存在是否删除
     */
    public static void rename(String oldname, String newname, boolean deleteNew) {
        if (!oldname.equals(newname)) {//新的文件名和以前文件名不同时,才有必要进行重命名
            File oldfile = new File(oldname);
            File newfile = new File(newname);
            if (newfile.exists() && deleteNew)//若在该目录下已经有一个文件和新文件名相同，则不允许重命名
            {
                newfile.delete();
            }

            oldfile.renameTo(newfile);

        }
    }

    /**
     * 获取文件编码信息
     *
     * @param file
     * @return
     */
    public static String getFileEncode(String file) {
        return EncodingDetect.getJavaEncode(file);
    }

    /**
     * 获取文件编码
     *
     * @param file
     * @return
     */
    public static String getFileEncode(File file) {
        return EncodingDetect.getJavaEncode(file.getAbsolutePath());
    }


    /**
     * 格式化大小为可阅读的文件格式
     *
     * @param bytes
     * @return
     */
    public static String formatBytes(double bytes) {
        String[] dictionary = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        int index = 0;
        for (index = 0; index < dictionary.length; index++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;
        }
        return String.format("%." + 2 + "f", bytes) + " " + dictionary[index];
    }

}
