package com.lingdonge.db.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Druid配置
 */
@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class DruidProperties {

    /**
     * 默认账号
     */
    private String druidUser = "admin";

    /**
     * 默认密码
     */
    private String druidPass = "admin123";

    /**
     * URL
     */
    private String url;

    /**
     * 多租户前缀
     */
    private String tenantUrl;

    /**
     * 多租户参数
     */
    private String tenantPara;

    /**
     * 数据库用户名
     */
    private String username = "root";

    /**
     * 数据库密码
     */
    private String password = "root";

    /**
     * 连接默认值
     */
    private String driverClassName = "com.mysql.jdbc.Driver";

    private int initialSize = 5;

    private int minIdle = 5;

    private int maxActive = 20;

    /**
     * 配置获取连接等待超时的时间
     */
    private int maxWait = 20 * 1000;

    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    private int timeBetweenEvictionRunsMillis = 60 * 1000;

    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    private int minEvictableIdleTimeMillis = 300 * 1000;

    private String validationQuery = "select 'x'";

    private boolean testWhileIdle = true;

    private boolean testOnBorrow = false;

    private boolean testOnReturn = false;

    private boolean poolPreparedStatements = true;

    private int maxPoolPreparedStatementPerConnectionSize = 20;

    /**
     * log4j进行日志输入，而springboot框架使用的是log4j2
     */
    private String filters = "stat,wall,log4j2";

    private String connectionProperties;

    /**
     * 是否记录慢查询日志
     */
    private String logSlowSql;

    private boolean useGlobalDataSourceStat = true;

    private boolean removeAbandoned = false;

    private int removeAbandonedTimeoutMillis = 300 * 1000;

    private boolean logAbandoned = false;

}
