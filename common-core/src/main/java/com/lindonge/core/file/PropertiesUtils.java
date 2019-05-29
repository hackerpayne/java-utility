package com.lindonge.core.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.sync.ReadWriteSynchronizer;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 从.properties文件中读取相关测试数据
 * 并非是线程安全的，多线程操作时，需要做处理
 */
@Slf4j
public class PropertiesUtils {

    private static volatile PropertiesUtils instance;

    private Configuration conf;

    private PropertiesUtils() {

    }

    public static void main(String[] args) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 10; i++) {

                    log.info(i + "开始");
                    String remoteUrl = new PropertiesUtils("/Users/kyle/JavaUtility/JavaUtility/testSelenium/configuration/configuration.properties").getProperty("");
                    System.out.println(remoteUrl);
                }
            }
        };
        new Thread(runnable).start();
        new Thread(runnable).start();

    }

    /**
     * 读取配置文件的类
     *
     * @param fileName
     */
    public PropertiesUtils(String fileName) {
        readConfig(fileName);
    }

    /**
     * 单例模式，线程安全
     *
     * @return
     */
    public static PropertiesUtils getInstance() {
        if (instance == null) {
            synchronized (PropertiesUtils.class) {
                if (instance == null) {
                    instance = new PropertiesUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 读取配置文件
     *
     * @param fileName
     */
    private synchronized void readConfig(String fileName) {
        Configurations configs = new Configurations();

        try {
            conf = configs.properties(new File(fileName));

            conf.setSynchronizer(new ReadWriteSynchronizer());

        } catch (ConfigurationException e) {
            log.error("PropertiesUtils.getProperties发生异常", e);
        }
    }

    /**
     * 读取配置文件，没取到值，使用null做为默认值
     *
     * @param key
     * @return
     */
    public synchronized String getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * 读取指定目录下面的配置文件的内容
     *
     * @param key          取值的Key
     * @param defaultValue 没取到值时，使用的默认值
     * @return
     */
    public synchronized String getProperty(String key, String defaultValue) {

        String val;
        try {
            val = conf.getProperty(key).toString();
            if (StringUtils.isEmpty(val)) {
                val = defaultValue;
            }
        } catch (Exception e) {
            log.error("无法从配置文件中获取配置：" + key);
            val = defaultValue;
        }

        return val;

    }

    /**
     * 从文件中直接读取配置
     *
     * @param config
     * @return
     * @throws IOException
     */
    public synchronized static Properties getProperties(String config) {
        Properties properties = new Properties();
        log.info("Get the configuration file: " + config);
        try {
            FileInputStream inStream = new FileInputStream(new File(config));
            properties.load(inStream);
        } catch (Exception e) {
            log.error("can't find the configuration file ", e);
        }

        return properties;
    }
}