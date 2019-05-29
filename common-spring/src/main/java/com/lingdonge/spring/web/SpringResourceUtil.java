package com.lingdonge.spring.web;

import com.lindonge.core.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Spring资源文件读取类
 */
@Slf4j
public class SpringResourceUtil {

    /**
     * 获取文件完整路径的方法
     *
     * @param path 文件名称
     * @return URL 文件完整路径
     */
    public static <T> URL findAsResource(Class<T> cls, String path) {
        URL url = null;
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            url = contextClassLoader.getResource(path);
        }
        if (url != null) {
            return url;
        }
        url = cls.getClassLoader().getResource(path);
        if (url != null) {
            return url;
        }
        url = ClassLoader.getSystemClassLoader().getResource(path);
        return url;
    }

    /**
     * 从相对路径中读取资源文件，兼容Jar包里面的资源文件读取。
     * 但是只能读取InputStream里面。如果要读取对象里面，使用：
     * InputStream inputStream = FileUtil.getFileStream(MODEL_PATH);
     * ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
     * NaiveBayesModel bean = (NaiveBayesModel) objectInputStream.readObject();
     *
     * @param relativePath
     * @return
     */
    public static InputStream getFileStream(String relativePath) {

        try {
            File destPath = FileUtils.getFile(relativePath);
            if (destPath.exists()) {
                return new FileInputStream(destPath);
            }

            destPath = FileUtils.getFile(Utils.CurrentDir, relativePath);
            if (destPath.exists()) {
                return new FileInputStream(destPath);
            }

            destPath = FileUtils.getFile(Utils.CurrentDir, "config", relativePath);
            if (destPath.exists()) {
                return new FileInputStream(destPath);
            }
            ClassPathResource classPathResource = new ClassPathResource(relativePath);
            return classPathResource.getInputStream();

        } catch (Exception ex) {
            log.error("getFileStream发生异常{}", ex.toString());
            return null;
        }
    }

}
