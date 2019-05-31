package com.lingdonge.core.file;

import com.lingdonge.core.file.zip.ZipUtil;
import com.lingdonge.core.util.Utils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtilTest {

    public static void main(String[] args) throws IOException {

        ZipOutputStream out = ZipUtil.getZipOutputStream(FileUtil.getFile(Utils.CurrentDir, "multithreading.zip"));

        // 添加文件进来
        InputStream stream = FileUtil.getInputStream(FileUtil.getFile(Utils.CurrentDir, "pom.xml"));
        out.putNextEntry(new ZipEntry("pom.xml"));
        IOUtils.copy(stream, out);
        out.flush();

        // 添加字符串进来
        String input = "中文内容测试";
        InputStream content = new ByteArrayInputStream(input.getBytes());
        out.putNextEntry(new ZipEntry("haha.js"));
        IOUtils.copy(content, out);

        out.flush();

        // 关闭流
        out.closeEntry();
        out.close();
    }

}