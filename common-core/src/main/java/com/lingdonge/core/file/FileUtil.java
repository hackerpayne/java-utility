package com.lingdonge.core.file;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.lingdonge.core.http.UrlUtils;
import com.lingdonge.core.util.JudgeUtil;
import com.lingdonge.core.util.StringUtils;
import com.lingdonge.core.util.Utils;
import com.lingdonge.core.collection.ArrayUtil;
import com.lingdonge.core.exceptions.IORuntimeException;
import com.lingdonge.core.reflect.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.testng.collections.Lists;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Kyle on 16/6/7.
 */
@Slf4j
public class FileUtil {

    /**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';

    /**
     * Class文件扩展名
     */
    public static final String CLASS_EXT = ".class";

    /**
     * Jar文件扩展名
     */
    public static final String JAR_FILE_EXT = ".jar";

    /**
     * 在Jar中的路径jar的扩展名形式
     */
    public static final String JAR_PATH_EXT = ".jar!";

    /**
     * 当Path为文件形式时, path会加入一个表示文件的前缀
     */
    public static final String PATH_FILE_PRE = UrlUtils.FILE_URL_PREFIX;

    public static void main(String[] args) throws IOException {

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
     * 获取一个BufferWriter对象
     *
     * @param filePath
     * @param encoding
     * @param append
     * @return
     */
    public static BufferedWriter getWriter(String filePath, String encoding, boolean append) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(new File(filePath), append);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BufferedWriter(fw);
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
        return loopFiles(file, new FileFilter() {

            @Override
            public boolean accept(File file) {
                final String path = file.getPath();
                if (StringUtils.isEmpty(filter)) return true;

                if (org.apache.commons.lang3.StringUtils.isNotEmpty(path) && path.toLowerCase().endsWith(filter)) {
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 读取目录下面所有满足条件的文件
     *
     * @param file
     * @param listFilter
     * @return
     */
    public static List<File> loopFiles(File file, String... listFilter) {
        return loopFiles(file, Arrays.asList(listFilter));
    }

    /**
     * 返回指定扩展名的文件列表
     *
     * @param file
     * @param listFilter
     * @return
     */
    public static List<File> loopFiles(File file, List<String> listFilter) {
        return loopFiles(file, new FileFilter() {

            @Override
            public boolean accept(File file) {
                final String path = file.getPath();
                if (listFilter.size() == 0) {
                    return true;
                }

                if (path != null) {
                    for (String filter : listFilter) {
                        if (path.toLowerCase().endsWith(filter)) {
                            return true;
                        }
                    }

                }
                return false;
            }
        });
    }

    /**
     * 递归遍历目录以及子目录中的所有文件<br>
     * 如果提供file为文件，直接返回过滤结果
     *
     * @param file       当前遍历文件或目录
     * @param fileFilter 文件过滤规则对象，选择要保留的文件，只对文件有效，不过滤目录
     * @return 文件列表
     */
    public static List<File> loopFiles(File file, FileFilter fileFilter) {
        List<File> fileList = new ArrayList<>();
        if (file == null) {
            return fileList;
        } else if (file.exists() == false) {
            return fileList;
        }

        if (file.isDirectory()) {
            for (File tmp : file.listFiles()) {
                fileList.addAll(loopFiles(tmp, fileFilter));
            }
        } else {
            if (null == fileFilter || fileFilter.accept(file)) {
                fileList.add(file);
            }
        }

        return fileList;
    }

    /**
     * 判断是否为文件，如果path为null，则返回false
     *
     * @param path 文件路径
     * @return 如果为文件true
     */
    public static boolean isFile(String path) {
        return (path == null) ? false : file(path).isFile();
    }

    /**
     * 判断是否为文件，如果file为null，则返回false
     *
     * @param file 文件
     * @return 如果为文件true
     */
    public static boolean isFile(File file) {
        return (file == null) ? false : file.isFile();
    }

    /**
     * 检查两个文件是否是同一个文件<br>
     * 所谓文件相同，是指File对象是否指向同一个文件或文件夹
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 是否相同
     * @throws IORuntimeException IO异常
     * @see Files#isSameFile(Path, Path)
     */
    public static boolean equals(File file1, File file2) throws IORuntimeException {
        Assert.notNull(file1);
        Assert.notNull(file2);
        // 如果其中有一个不存在，则肯定不相等
        if (!file1.exists() || !file2.exists()) {
            return false;
        }

        try {
            return Files.isSameFile(file1.toPath(), file2.toPath());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 获得最后一个文件路径分隔符的位置
     *
     * @param filePath 文件路径
     * @return 最后一个文件路径分隔符的位置
     */
    public static int indexOfLastSeparator(String filePath) {
        if (filePath == null) {
            return -1;
        }
        int lastUnixPos = filePath.lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = filePath.lastIndexOf(WINDOWS_SEPARATOR);
        return (lastUnixPos >= lastWindowsPos) ? lastUnixPos : lastWindowsPos;
    }

    /**
     * 判断文件是否被改动<br>
     * 如果文件对象为 null 或者文件不存在，被视为改动
     *
     * @param file           文件对象
     * @param lastModifyTime 上次的改动时间
     * @return 是否被改动
     */
    public static boolean isModifed(File file, long lastModifyTime) {
        if (null == file || false == file.exists()) {
            return true;
        }
        return file.lastModified() != lastModifyTime;
    }

    /**
     * 返回主文件名
     *
     * @param file 文件
     * @return 主文件名
     */
    public static String mainName(File file) {
        if (file.isDirectory()) {
            return file.getName();
        }
        return mainName(file.getName());
    }

    /**
     * 返回主文件名
     *
     * @param fileName 完整文件名
     * @return 主文件名
     */
    public static String mainName(String fileName) {
        if (StringUtils.isBlank(fileName) || false == fileName.contains(StringUtils.DOT)) {
            return fileName;
        }
        return StringUtils.subPre(fileName, fileName.lastIndexOf(StringUtils.DOT));
    }

    /**
     * 获取文件扩展名，扩展名不带“.”
     *
     * @param file 文件
     * @return 扩展名
     */
    public static String extName(File file) {
        if (null == file) {
            return null;
        }
        if (file.isDirectory()) {
            return null;
        }
        return extName(file.getName());
    }

    /**
     * 获得文件的扩展名，扩展名不带“.”
     *
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String extName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int index = fileName.lastIndexOf(StringUtils.DOT);
        if (index == -1) {
            return StringUtils.EMPTY;
        } else {
            String ext = fileName.substring(index + 1);
            // 扩展名中不能包含路径相关的符号
            return (ext.contains(String.valueOf(UNIX_SEPARATOR)) || ext.contains(String.valueOf(WINDOWS_SEPARATOR))) ? StringUtils.EMPTY : ext;
        }
    }

    /**
     * 判断文件路径是否有指定后缀，忽略大小写<br>
     * 常用语判断扩展名
     *
     * @param file   文件或目录
     * @param suffix 后缀
     * @return 是否有指定后缀
     */
    public static boolean pathEndsWith(File file, String suffix) {
        return file.getPath().toLowerCase().endsWith(suffix);
    }

    /**
     * 获得输入流
     *
     * @param file 文件
     * @return 输入流
     * @throws IORuntimeException 文件未找到
     */
    public static BufferedInputStream getInputStream(File file) throws IORuntimeException {
        try {
            return new BufferedInputStream(new FileInputStream(file));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 获得输入流
     *
     * @param path 文件路径
     * @return 输入流
     * @throws IORuntimeException 文件未找到
     */
    public static BufferedInputStream getInputStream(String path) throws IORuntimeException {
        return getInputStream(file(path));
    }

    /**
     * 获得BOM输入流，用于处理带BOM头的文件
     *
     * @param file 文件
     * @return 输入流
     * @throws IORuntimeException 文件未找到
     */
    public static BOMInputStream getBOMInputStream(File file) throws IORuntimeException {
        try {
            return new BOMInputStream(new FileInputStream(file));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
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
            LineIterator.closeQuietly(it);
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
            IOUtils.closeQuietly(fw);
        }
    }

    /**
     * 获得一个输出流对象
     *
     * @param file 文件
     * @return 输出流对象
     * @throws IORuntimeException IO异常
     */
    public static BufferedOutputStream getOutputStream(File file) throws IORuntimeException {
        try {
            return new BufferedOutputStream(new FileOutputStream(touch(file)));
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 获得一个输出流对象
     *
     * @param path 输出到的文件路径，绝对路径
     * @return 输出流对象
     * @throws IORuntimeException IO异常
     */
    public static BufferedOutputStream getOutputStream(String path) throws IORuntimeException {
        return getOutputStream(touch(path));
    }

    /**
     * 可读的文件大小
     *
     * @param file 文件
     * @return 大小
     */
    public static String readableFileSize(File file) {
        return readableFileSize(file.length());
    }

    /**
     * 可读的文件大小<br>
     * 参考 http://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
     *
     * @param size Long类型大小
     * @return 大小
     */
    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB", "EB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 获取相对的文件路径，如果有此文件，直接返回，否则在根目录下面进行寻找
     *
     * @param filePath
     * @return
     */
    public static File getRelativeFile(String filePath) {
        File file = new File(filePath);

        if (file.exists()) return file;

        file = FileUtil.getFile(Utils.CurrentDir, filePath);

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

        return ArrayUtil.getRandomItem(fileLines);
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
        if (!file.exists())
            file.createNewFile();
    }

    /**
     * 删除文件
     *
     * @param path
     * @param filename
     */
    public static void delFile(String path, String filename) {
        File file = new File(path + "/" + filename);
        if (file.exists() && file.isFile()) {
            file.delete();
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
        if (!file.exists() || file.isDirectory())
            throw new FileNotFoundException();
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
        if (!file.exists())
            file.createNewFile();
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
                newfile.delete();

            oldfile.renameTo(newfile);

        }
    }

//    /**
//     * @param fileName
//     * @param maxSize
//     * @return
//     * @throws IOException
//     */
//    public static byte[] readAllBytes(String fileName, long maxSize) throws IOException {
//        Path path = Paths.get(fileName);
//        long size = Files.size(path);
//        if (size > maxSize) {
//            throw new IOException("file: " + path + ", size:" + size + "> " + maxSize);
//        }
//        return Files.readAllBytes(path);
//    }
//
//    /**
//     * @param fileName 文件名
//     * @param charset  编码
//     * @param maxSize  最大可读的大小
//     * @return
//     * @throws IOException
//     */
//    public static List<String> readAlllines(String fileName, String charset, long maxSize) throws IOException {
//        Path path = Paths.get(fileName);
//        long size = Files.size(path);
//        if (size > maxSize && maxSize > 0) {
//            throw new IOException("file: " + path + ", size:" + size + "> " + maxSize);
//        }
//        Charset chars = Charset.forName(charset);
//        return Files.readAllLines(path, chars);
//    }


    public static File getFile(File directory, String... names) {
        if (directory == null) {
            throw new NullPointerException("directory must not be null");
        } else if (names == null) {
            throw new NullPointerException("names must not be null");
        } else {
            File file = directory;
            String[] arr$ = names;
            int len$ = names.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String name = arr$[i$];
                file = new File(file, name);
            }

            return file;
        }
    }

    /**
     * 合并多个路径形成新的文件
     *
     * @param names 路径列表
     * @return
     */
    public static File getFile(String... names) {
        if (names == null) {
            throw new NullPointerException("names must not be null");
        } else {
            File file = null;
            String[] arr$ = names;
            int len$ = names.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String name = arr$[i$];
                if (file == null) {
                    file = new File(name);
                } else {
                    file = new File(file, name);
                }
            }

            return file;
        }
    }

    /**
     * 文件是否为空<br>
     * 目录：里面没有文件时为空 文件：文件大小为0时为空
     *
     * @param file 文件
     * @return 是否为空，当提供非目录时，返回false
     */
    public static boolean isEmpty(File file) {
        if (null == file) {
            return true;
        }

        if (file.isDirectory()) {
            String[] subFiles = file.list();
            if (JudgeUtil.isEmpty(subFiles)) {
                return true;
            }
        } else if (file.isFile()) {
            return file.length() <= 0;
        }

        return false;
    }

    /**
     * 目录是否为空
     *
     * @param file 目录
     * @return 是否为空，当提供非目录时，返回false
     */
    public static boolean isNotEmpty(File file) {
        return false == isEmpty(file);
    }

    /**
     * 创建File对象
     *
     * @param parent 父目录
     * @param path   文件路径
     * @return File
     */
    public static File file(String parent, String path) {
        if (StringUtils.isBlank(path)) {
            throw new NullPointerException("File path is blank!");
        }
        return new File(parent, path);
    }

    /**
     * 创建File对象
     *
     * @param parent 父文件对象
     * @param path   文件路径
     * @return File
     */
    public static File file(File parent, String path) {
        if (StringUtils.isBlank(path)) {
            throw new NullPointerException("File path is blank!");
        }
        return new File(parent, path);
    }

    /**
     * 创建File对象
     *
     * @param uri 文件URI
     * @return File
     */
    public static File file(URI uri) {
        if (uri == null) {
            throw new NullPointerException("File uri is null!");
        }
        return new File(uri);
    }

    /**
     * 创建File对象
     *
     * @param url 文件URL
     * @return File
     */
    public static File file(URL url) {
        return new File(UrlUtils.toURI(url));
    }

    /**
     * @param file
     * @return
     */
    public static File file(String file) {
        return new File(file);
    }

    /**
     * 判断文件是否存在，如果path为null，则返回false
     *
     * @param path 文件路径
     * @return 如果存在返回true
     */
    public static boolean exist(String path) {
        return (path == null) ? false : file(path).exists();
    }

    /**
     * 判断文件是否存在，如果file为null，则返回false
     *
     * @param file 文件
     * @return 如果存在返回true
     */
    public static boolean exist(File file) {
        return (file == null) ? false : file.exists();
    }

    /**
     * 是否存在匹配文件
     *
     * @param directory 文件夹路径
     * @param regexp    文件夹中所包含文件名的正则表达式
     * @return 如果存在匹配文件返回true
     */
    public static boolean exist(String directory, String regexp) {
        File file = new File(directory);
        if (!file.exists()) {
            return false;
        }

        String[] fileList = file.list();
        if (fileList == null) {
            return false;
        }

        for (String fileName : fileList) {
            if (fileName.matches(regexp)) {
                return true;
            }

        }
        return false;
    }

    /**
     * 指定文件最后修改时间
     *
     * @param file 文件
     * @return 最后修改时间
     */
    public static Date lastModifiedTime(File file) {
        if (!exist(file)) {
            return null;
        }

        return new Date(file.lastModified());
    }

    /**
     * 指定路径文件最后修改时间
     *
     * @param path 绝对路径
     * @return 最后修改时间
     */
    public static Date lastModifiedTime(String path) {
        return lastModifiedTime(new File(path));
    }

    /**
     * 计算目录或文件的总大小<br>
     * 当给定对象为文件时，直接调用 {@link File#length()}<br>
     * 当给定对象为目录时，遍历目录下的所有文件和目录，递归计算其大小，求和返回
     *
     * @param file 目录或文件
     * @return 总大小
     */
    public static long size(File file) {
        Assert.notNull(file, "file argument is null !");
        if (false == file.exists()) {
            throw new IllegalArgumentException(StringUtils.format("File [{}] not exist !", file.getAbsolutePath()));
        }

        if (file.isDirectory()) {
            long size = 0L;
            File[] subFiles = file.listFiles();
            if (JudgeUtil.isEmpty(subFiles)) {
                return 0L;// empty directory
            }
            for (int i = 0; i < subFiles.length; i++) {
                size += size(subFiles[i]);
            }
            return size;
        } else {
            return file.length();
        }
    }

    /**
     * 给定文件或目录的最后修改时间是否晚于给定时间
     *
     * @param file      文件或目录
     * @param reference 参照文件
     * @return 是否晚于给定时间
     */
    public static boolean newerThan(File file, File reference) {
        if (null == file || false == reference.exists()) {
            return true;// 文件一定比一个不存在的文件新
        }
        return newerThan(file, reference.lastModified());
    }

    /**
     * 给定文件或目录的最后修改时间是否晚于给定时间
     *
     * @param file       文件或目录
     * @param timeMillis 做为对比的时间
     * @return 是否晚于给定时间
     */
    public static boolean newerThan(File file, long timeMillis) {
        if (null == file || false == file.exists()) {
            return false;// 不存在的文件一定比任何时间旧
        }
        return file.lastModified() > timeMillis;
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param fullFilePath 文件的全路径，使用POSIX风格
     * @return 文件，若路径为null，返回null
     * @throws IORuntimeException IO异常
     */
    public static File touch(String fullFilePath) throws IORuntimeException {
        if (fullFilePath == null) {
            return null;
        }
        return touch(file(fullFilePath));
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param file 文件对象
     * @return 文件，若路径为null，返回null
     * @throws IORuntimeException IO异常
     */
    public static File touch(File file) throws IORuntimeException {
        if (null == file) {
            return null;
        }
        if (false == file.exists()) {
            mkParentDirs(file);
            try {
                file.createNewFile();
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }
        return file;
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param parent 父文件对象
     * @param path   文件路径
     * @return File
     * @throws IORuntimeException IO异常
     */
    public static File touch(File parent, String path) throws IORuntimeException {
        return touch(file(parent, path));
    }

    /**
     * 创建文件及其父目录，如果这个文件存在，直接返回这个文件<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param parent 父文件对象
     * @param path   文件路径
     * @return File
     * @throws IORuntimeException IO异常
     */
    public static File touch(String parent, String path) throws IORuntimeException {
        return touch(file(parent, path));
    }

    /**
     * 创建所给文件或目录的父目录
     *
     * @param file 文件或目录
     * @return 父目录
     */
    public static File mkParentDirs(File file) {
        final File parentFile = file.getParentFile();
        if (null != parentFile && false == parentFile.exists()) {
            parentFile.mkdirs();
        }
        return parentFile;
    }

    /**
     * 创建父文件夹，如果存在直接返回此文件夹
     *
     * @param path 文件夹路径，使用POSIX格式，无论哪个平台
     * @return 创建的目录
     */
    public static File mkParentDirs(String path) {
        if (path == null) {
            return null;
        }
        return mkParentDirs(file(path));
    }

    /**
     * 删除文件或者文件夹<br>
     * 路径如果为相对路径，会转换为ClassPath路径！ 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param fullFileOrDirPath 文件或者目录的路径
     * @return 成功与否
     * @throws IORuntimeException IO异常
     */
    public static boolean del(String fullFileOrDirPath) throws IORuntimeException {
        return del(file(fullFileOrDirPath));
    }

    /**
     * 删除文件或者文件夹<br>
     * 注意：删除文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param file 文件对象
     * @return 成功与否
     * @throws IORuntimeException IO异常
     */
    public static boolean del(File file) throws IORuntimeException {
        if (file == null || file.exists() == false) {
            return true;
        }

        if (file.isDirectory()) {
            clean(file);
        }
        return file.delete();
    }

    /**
     * 清空文件夹<br>
     * 注意：清空文件夹时不会判断文件夹是否为空，如果不空则递归删除子文件或文件夹<br>
     * 某个文件删除失败会终止删除操作
     *
     * @param directory 文件夹
     * @return 成功与否
     * @throws IORuntimeException IO异常
     * @since 3.0.6
     */
    public static boolean clean(File directory) throws IORuntimeException {
        if (directory == null || directory.exists() == false || false == directory.isDirectory()) {
            return true;
        }

        final File[] files = directory.listFiles();
        for (File childFile : files) {
            boolean isOk = del(childFile);
            if (isOk == false) {
                // 删除一个出错则本次删除任务失败
                return false;
            }
        }
        return true;
    }

    /**
     * 创建文件夹，如果存在直接返回此文件夹<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param dirPath 文件夹路径，使用POSIX格式，无论哪个平台
     * @return 创建的目录
     */
    public static File mkdir(String dirPath) {
        if (dirPath == null) {
            return null;
        }
        final File dir = file(dirPath);
        return mkdir(dir);
    }

    /**
     * 创建文件夹，会递归自动创建其不存在的父文件夹，如果存在直接返回此文件夹<br>
     * 此方法不对File对象类型做判断，如果File不存在，无法判断其类型
     *
     * @param dir 目录
     * @return 创建的目录
     */
    public static File mkdir(File dir) {
        if (dir == null) {
            return null;
        }
        if (false == dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 创建临时文件<br>
     * 创建后的文件名为 prefix[Randon].tmp
     *
     * @param dir 临时文件创建的所在目录
     * @return 临时文件
     * @throws IORuntimeException IO异常
     */
    public static File createTempFile(File dir) throws IORuntimeException {
        return createTempFile("hutool", null, dir, true);
    }

    /**
     * 创建临时文件<br>
     * 创建后的文件名为 prefix[Randon].tmp
     *
     * @param dir       临时文件创建的所在目录
     * @param isReCreat 是否重新创建文件（删掉原来的，创建新的）
     * @return 临时文件
     * @throws IORuntimeException IO异常
     */
    public static File createTempFile(File dir, boolean isReCreat) throws IORuntimeException {
        return createTempFile("hutool", null, dir, isReCreat);
    }

    /**
     * 创建临时文件<br>
     * 创建后的文件名为 prefix[Randon].suffix From com.jodd.io.FileUtil
     *
     * @param prefix    前缀，至少3个字符
     * @param suffix    后缀，如果null则使用默认.tmp
     * @param dir       临时文件创建的所在目录
     * @param isReCreat 是否重新创建文件（删掉原来的，创建新的）
     * @return 临时文件
     * @throws IORuntimeException IO异常
     */
    public static File createTempFile(String prefix, String suffix, File dir, boolean isReCreat) throws IORuntimeException {
        int exceptionsCount = 0;
        while (true) {
            try {
                File file = File.createTempFile(prefix, suffix, dir).getCanonicalFile();
                if (isReCreat) {
                    file.delete();
                    file.createNewFile();
                }
                return file;
            } catch (IOException ioex) { // fixes java.io.WinNTFileSystem.createFileExclusively access denied
                if (++exceptionsCount >= 50) {
                    throw new IORuntimeException(ioex);
                }
            }
        }
    }

    /**
     * 通过JDK7+的 {@link Files#copy(Path, Path, CopyOption...)} 方法拷贝文件
     *
     * @param src     源文件路径
     * @param dest    目标文件或目录路径，如果为目录使用与源文件相同的文件名
     * @param options {@link StandardCopyOption}
     * @return File
     * @throws IORuntimeException IO异常
     */
    public static File copyFile(String src, String dest, StandardCopyOption... options) throws IORuntimeException {
        Assert.notBlank(src, "Source File path is blank !");
        Assert.notNull(src, "Destination File path is null !");
        return copyFile(Paths.get(src), Paths.get(dest), options).toFile();
    }

    /**
     * 通过JDK7+的 {@link Files#copy(Path, Path, CopyOption...)} 方法拷贝文件
     *
     * @param src     源文件
     * @param dest    目标文件或目录，如果为目录使用与源文件相同的文件名
     * @param options {@link StandardCopyOption}
     * @return File
     * @throws IORuntimeException IO异常
     */
    public static File copyFile(File src, File dest, StandardCopyOption... options) throws IORuntimeException {
        // check
        Assert.notNull(src, "Source File is null !");
        if (false == src.exists()) {
            throw new IORuntimeException("File not exist: " + src);
        }
        Assert.notNull(dest, "Destination File or directiory is null !");
        if (equals(src, dest)) {
            throw new IORuntimeException("Files '" + src + "' and '" + dest + "' are equal");
        }

        Path srcPath = src.toPath();
        Path destPath = dest.isDirectory() ? dest.toPath().resolve(srcPath.getFileName()) : dest.toPath();
        try {
            return Files.copy(srcPath, destPath, options).toFile();
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 通过JDK7+的 {@link Files#copy(Path, Path, CopyOption...)} 方法拷贝文件
     *
     * @param src     源文件路径
     * @param dest    目标文件或目录，如果为目录使用与源文件相同的文件名
     * @param options {@link StandardCopyOption}
     * @return Path
     * @throws IORuntimeException IO异常
     */
    public static Path copyFile(Path src, Path dest, StandardCopyOption... options) throws IORuntimeException {
        Assert.notNull(src, "Source File is null !");
        Assert.notNull(dest, "Destination File or directiory is null !");

        Path destPath = dest.toFile().isDirectory() ? dest.resolve(src.getFileName()) : dest;
        try {
            return Files.copy(src, destPath, options);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * 获取标准的绝对路径
     *
     * @param file 文件
     * @return 绝对路径
     */
    public static String getAbsolutePath(File file) {
        if (file == null) {
            return null;
        }

        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }

    /**
     * 给定路径已经是绝对路径<br>
     * 此方法并没有针对路径做标准化，建议先执行{@link #(String)}方法标准化路径后判断
     *
     * @param path 需要检查的Path
     * @return 是否已经是绝对路径
     */
    public static boolean isAbsolutePath(String path) {
        if (StringUtils.C_SLASH == path.charAt(0) || path.matches("^[a-zA-Z]:/.*")) {
            // 给定的路径已经是绝对路径了
            return true;
        }
        return false;
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

}
