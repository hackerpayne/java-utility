package com.lingdonge.db.db;

import com.lindonge.core.file.PropertiesUtils;
import com.lindonge.core.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.util.HashMap;

/**
 * 简版的JDBC辅助类
 */
@Slf4j
public class JdbcHelper {

    public static HashMap<String, JdbcTemplate> templateMap = new HashMap<String, JdbcTemplate>();

    private static volatile JdbcTemplate instance;

    /**
     * 单例模式，线程安全
     *
     * @return
     * @throws Exception
     */
    public static JdbcTemplate getIstance() throws Exception {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (instance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (JdbcHelper.class) {
                //未初始化，则初始instance变量
                if (instance == null) {

                    File configFile = FileUtils.getFile(Utils.CurrentDir, "configuration", "database.properties");

                    if (!configFile.exists()) {
                        throw new Exception("配置文件:【" + configFile.getPath() + "】不存在，无法使用Redis");
                    } else {
                        PropertiesUtils propertiesUtils = new PropertiesUtils(configFile.getPath());

                        String url = propertiesUtils.getProperty("jdbc.url", "");
                        String userName = propertiesUtils.getProperty("jdbc.username", "");
                        String password = propertiesUtils.getProperty("jdbc.password", "");

                        String min = propertiesUtils.getProperty("jdbc.min", "0");
                        String max = propertiesUtils.getProperty("jdbc.max", "10");

                        instance = JdbcHelper.createMysqlTemplate(url, url, userName, password, Integer.parseInt(min), Integer.parseInt(max));

                    }


                }
            }
        }
        return instance;
    }

    /**
     * 创建一个JDBC连接类
     *
     * @param templateName
     * @param url
     * @param username
     * @param password
     * @param initialSize
     * @param maxActive
     * @return
     */
    public static JdbcTemplate createMysqlTemplate(String templateName,
                                                   String url, String username, String password,
                                                   int initialSize, int maxActive) {

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        JdbcTemplate template = new JdbcTemplate(dataSource);
        templateMap.put(templateName, template);
        return template;
    }

    public static JdbcTemplate getJdbcTemplate(String templateName) {
        return templateMap.get(templateName);
    }

}
