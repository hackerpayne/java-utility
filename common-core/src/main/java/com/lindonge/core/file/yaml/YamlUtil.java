package com.lindonge.core.file.yaml;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * SnakeYaml
 * https://bitbucket.org/asomov/snakeyaml/wiki/Documentation
 */
public class YamlUtil {

    /**
     * 文件转实体对象
     *
     * @param resourceFilePath
     * @param cls
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T loadYamlAs(String resourceFilePath, Class<T> cls) throws IOException {
        T ret = new Yaml().loadAs(fileToStream(resourceFilePath), cls);
        return ret;
    }

    /**
     * 文件转Map对象
     *
     * @param resourceFilePath
     * @return
     * @throws IOException
     */
    public static Map loadYamlAsMap(String resourceFilePath) throws IOException {
        return (Map) loadYamlAsObj(fileToStream(resourceFilePath));
    }

    public static Properties loadYamlAsProperties(String resourceFilePath) throws IOException {
        return YamlToPropertiesConverter.convertToProperties(fileToStream(resourceFilePath));
    }

    /**
     * 文件转Stream流对象
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static InputStream fileToStream(String filePath) throws IOException {
        InputStream stream = null;

        URL url = YamlUtil.class.getClassLoader().getResource(filePath);
//       stream = YamlUtil.class.getResourceAsStream(filePath) // 会抛异常，流被重复读
        if (url != null) {
            stream = new FileInputStream(url.getFile());
        }

        if (null == stream) {

            // 先识别conf目录下面
            File newPath = FileUtils.getFile(new File(System.getProperty("user.dir")), "conf", filePath);
            if (!newPath.exists()) {
                newPath = FileUtils.getFile(new File(System.getProperty("user.dir")), "config", filePath);
            }

            if (newPath.exists()) {
                stream = new FileInputStream(newPath);
            }
        }

        return IOUtils.toBufferedInputStream(stream);
    }

    /**
     * 文件读取到Obj对象
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static Object loadYamlAsObj(String filePath) throws IOException {
        return loadYamlAsObj(fileToStream(filePath));
    }

    /**
     * 流读取为Object对象
     *
     * @param inputStream
     * @return
     */
    public static Object loadYamlAsObj(InputStream inputStream) {
        try {
            return new Yaml().load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对象按Yaml格式保存至文件中
     *
     * @param clz
     * @param filePath
     * @param <T>
     * @throws IOException
     */
    public static <T> void saveYamlAs(Class<T> clz, String filePath) throws IOException {
        new Yaml().dump(clz, new FileWriter(filePath));
    }

    public static void main(String[] args) throws IOException {

        Map map = loadYamlAsMap("application-local.yml");
        System.out.println(map);

        Properties properties = loadYamlAsProperties("hbase.yml");
        System.out.println("=====properties=====");
        System.out.println(properties.getProperty("hbase.client.scanner.timeout.period"));

        Object obj = YamlUtil.loadYamlAsObj("application-local.yml");
        System.out.println(obj);

//        ModelProfile profile = loadYamlAs("application-local.yml", ModelProfile.class);
//        System.out.println(profile.toString());

    }

}
