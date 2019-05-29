package com.lingdonge.db.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.lingdonge.db.configuration.properties.DruidProperties;
import com.lingdonge.db.configuration.properties.HiKariDataSourceProperties;
import com.mysql.cj.jdbc.MysqlXADataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Druid、Mysql、Hikari数据源封装工具类
 */
@Slf4j
public class DataSourceBuilder {

    /**
     * 默认的数据源KEY
     */
    public static final String DEFAULT_DATASOURCE_KEY = "defaultDataSource";

    /**
     * MysqlXA数据源名称
     */
    public static String MYSQL_XA_CLASS_NAME = "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource";

    /**
     * Druid数据源名称
     */
    public static String DRUID_CLASS_NAME = "com.alibaba.druid.pool.DruidDataSource";

    /**
     * DruidXA数据源名称
     */
    public static String DRUID_XA_CLASS_NAME = "com.alibaba.druid.pool.xa.DruidXADataSource";

    /**
     * Hikari数据源名称
     */
    public static String HIKARI_CLASS_NAME = "com.zaxxer.hikari.HikariDataSource";

    /**
     * 使用MySQLXADatasource数据源
     *
     * @return
     */
    public static MysqlXADataSource createMysqlXADataSource(Properties properties) throws SQLException {
        MysqlXADataSource mysqlXADataSource = new MysqlXADataSource();
        mysqlXADataSource.setUrl("");
        mysqlXADataSource.setPinGlobalTxToPhysicalConnection(true);
        mysqlXADataSource.setUser("");
        mysqlXADataSource.setPassword("");
        return mysqlXADataSource;
    }

    /**
     * 根据环境前缀，创建Druid配置文件的配置信息
     *
     * @param env
     * @param prefix
     * @return
     */
    public static Properties buildDruidToProperties(Environment env, String prefix) {
        Properties prop = new Properties();
        prop.put("url", env.getProperty(prefix + "url"));
        prop.put("username", env.getProperty(prefix + "username"));
        prop.put("password", env.getProperty(prefix + "password"));
        prop.put("driverClassName", env.getProperty(prefix + "driverClassName", ""));
        prop.put("initialSize", env.getProperty(prefix + "initialSize", Integer.class));
        prop.put("maxActive", env.getProperty(prefix + "maxActive", Integer.class));
        prop.put("minIdle", env.getProperty(prefix + "minIdle", Integer.class));
        prop.put("maxWait", env.getProperty(prefix + "maxWait", Integer.class));
        prop.put("poolPreparedStatements", env.getProperty(prefix + "poolPreparedStatements", Boolean.class));

        prop.put("maxPoolPreparedStatementPerConnectionSize", env.getProperty(prefix + "maxPoolPreparedStatementPerConnectionSize", Integer.class));

        prop.put("validationQuery", env.getProperty(prefix + "validationQuery"));
        prop.put("validationQueryTimeout", env.getProperty(prefix + "validationQueryTimeout", Integer.class));
        prop.put("testOnBorrow", env.getProperty(prefix + "testOnBorrow", Boolean.class));
        prop.put("testOnReturn", env.getProperty(prefix + "testOnReturn", Boolean.class));
        prop.put("testWhileIdle", env.getProperty(prefix + "testWhileIdle", Boolean.class));
        prop.put("timeBetweenEvictionRunsMillis", env.getProperty(prefix + "timeBetweenEvictionRunsMillis", Integer.class));
        prop.put("minEvictableIdleTimeMillis", env.getProperty(prefix + "minEvictableIdleTimeMillis", Integer.class));
        prop.put("filters", env.getProperty(prefix + "filters"));
        return prop;
    }

    /**
     * 从环境前缀里面读取配置文件
     *
     * @param env
     * @param prefix
     * @return
     */
    public static DruidProperties buildDruidProperties(Environment env, String prefix) {
        DruidProperties druidProperties = new DruidProperties();

        druidProperties.setUrl(env.getProperty(prefix + "url"));
        druidProperties.setUsername(env.getProperty(prefix + "username"));
        druidProperties.setPassword(env.getProperty(prefix + "password"));
        druidProperties.setDriverClassName(env.getProperty(prefix + "driverClassName", ""));

        druidProperties.setInitialSize(env.getProperty(prefix + "initialSize", Integer.class));
        druidProperties.setMaxActive(env.getProperty(prefix + "maxActive", Integer.class));
        druidProperties.setMinIdle(env.getProperty(prefix + "minIdle", Integer.class));
        druidProperties.setMaxWait(env.getProperty(prefix + "maxWait", Integer.class));
        druidProperties.setPoolPreparedStatements(env.getProperty(prefix + "poolPreparedStatements", Boolean.class));
        druidProperties.setMaxPoolPreparedStatementPerConnectionSize(env.getProperty(prefix + "maxPoolPreparedStatementPerConnectionSize", Integer.class));
        druidProperties.setValidationQuery(env.getProperty(prefix + "validationQuery"));
        druidProperties.setTestOnBorrow(env.getProperty(prefix + "testOnBorrow", Boolean.class));
        druidProperties.setTestOnReturn(env.getProperty(prefix + "testOnReturn", Boolean.class));
        druidProperties.setTestWhileIdle(env.getProperty(prefix + "testWhileIdle", Boolean.class));
        druidProperties.setTimeBetweenEvictionRunsMillis(env.getProperty(prefix + "timeBetweenEvictionRunsMillis", Integer.class));
        druidProperties.setMinEvictableIdleTimeMillis(env.getProperty(prefix + "minEvictableIdleTimeMillis", Integer.class));
        druidProperties.setFilters(env.getProperty(prefix + "filters"));

        return druidProperties;
    }

    /**
     * 根据配置创建Druid数据源信息
     *
     * @param properties
     * @return
     */
    public static DruidDataSource createDruidDataSource(DruidProperties properties) {
        DruidDataSource datasource = new DruidDataSource();

        // 基本连接信息
        datasource.setUrl(properties.getUrl());
        datasource.setUsername(properties.getUsername());
        datasource.setPassword(properties.getPassword());
        datasource.setDriverClassName(properties.getDriverClassName());

        // 连接池相关设置
        datasource.setInitialSize(properties.getInitialSize());
        datasource.setMinIdle(properties.getMinIdle());
        datasource.setMaxActive(properties.getMaxActive());
        datasource.setMaxWait(properties.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        datasource.setValidationQuery(properties.getValidationQuery());
        datasource.setTestWhileIdle(properties.isTestWhileIdle());
        datasource.setTestOnBorrow(properties.isTestOnBorrow());
        datasource.setTestOnReturn(properties.isTestOnReturn());
        datasource.setPoolPreparedStatements(properties.isPoolPreparedStatements());
        datasource.setUseGlobalDataSourceStat(properties.isUseGlobalDataSourceStat());

        List<String> listInitSQL = new ArrayList<>();
        // 不加utf8mb4的话，在插入表情符号的时候，会报错，改了这里还需要把表的含表情的字段改为utf8mb4的字段格式才可以，否则依然会报错。
        //在连接字符串里面，不需要设置成utf8mb4，保留utf8就行了
        listInitSQL.add("set names utf8mb4;");
        datasource.setConnectionInitSqls(listInitSQL);

        datasource.setMaxPoolPreparedStatementPerConnectionSize(properties.getMaxPoolPreparedStatementPerConnectionSize());
        try {
            if (StringUtils.isNotEmpty(properties.getFilters())) {
                datasource.setFilters(properties.getFilters());
            }

//            List<Filter> filterList=new ArrayList<Filter>();
//            filterList.add(wallFilter());
//            datasource.setProxyFilters(filterList);

        } catch (SQLException e) {
            log.error("druid configuration initialization filter", e);
        }
        datasource.setConnectionProperties(properties.getConnectionProperties());

        return datasource;
    }

    /**
     * 创建XA分布式事务中使用的XA数据源
     *
     * @param properties
     * @return
     */
    public static DruidXADataSource createDruidXADataSource(DruidProperties properties) {
        DruidXADataSource datasource = new DruidXADataSource();

        // 基本连接信息
        datasource.setUrl(properties.getUrl());
        datasource.setUsername(properties.getUsername());
        datasource.setPassword(properties.getPassword());
        datasource.setDriverClassName(properties.getDriverClassName());

        // 连接池相关设置
        datasource.setInitialSize(properties.getInitialSize());
        datasource.setMinIdle(properties.getMinIdle());
        datasource.setMaxActive(properties.getMaxActive());
        datasource.setMaxWait(properties.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        datasource.setValidationQuery(properties.getValidationQuery());
        datasource.setTestWhileIdle(properties.isTestWhileIdle());
        datasource.setTestOnBorrow(properties.isTestOnBorrow());
        datasource.setTestOnReturn(properties.isTestOnReturn());
        datasource.setPoolPreparedStatements(properties.isPoolPreparedStatements());
        datasource.setUseGlobalDataSourceStat(properties.isUseGlobalDataSourceStat());

        // 分布事事务独有的几个参数
        datasource.setRemoveAbandoned(properties.isRemoveAbandoned()); // 是否打开连接泄露自动检测
        datasource.setRemoveAbandonedTimeoutMillis(properties.getRemoveAbandonedTimeoutMillis());//连接长时间没有使用，被认为发生泄露时长
        datasource.setLogAbandoned(properties.isLogAbandoned());//发生泄露时是否需要输出 log，建议在开启连接泄露检测时开启，方便排错

        List<String> listInitSQL = new ArrayList<>();
        // 不加utf8mb4的话，在插入表情符号的时候，会报错，改了这里还需要把表的含表情的字段改为utf8mb4的字段格式才可以，否则依然会报错。
        //在连接字符串里面，不需要设置成utf8mb4，保留utf8就行了
        listInitSQL.add("set names utf8mb4;");
        datasource.setConnectionInitSqls(listInitSQL);

        //只要maxPoolPreparedStatementPerConnectionSize>0,poolPreparedStatements就会被自动设定为true，参照druid的源码
        datasource.setMaxPoolPreparedStatementPerConnectionSize(properties.getMaxPoolPreparedStatementPerConnectionSize());

        try {
            if (StringUtils.isNotEmpty(properties.getFilters())) {
                datasource.setFilters(properties.getFilters());
            }
//            List<Filter> filterList=new ArrayList<Filter>();
//            filterList.add(wallFilter());
//            datasource.setProxyFilters(filterList);

        } catch (SQLException e) {
            log.error("druid configuration initialization filter", e);
        }
        datasource.setConnectionProperties(properties.getConnectionProperties());

        return datasource;
    }

    /**
     * 根据配置创建Hikari数据源
     *
     * @param dataSourceProperties
     * @return
     */
    public static HikariDataSource createHikariDataSource(HiKariDataSourceProperties dataSourceProperties) {
        Properties dsProps = new Properties();
        dsProps.put("url", dataSourceProperties.getUrl());
        dsProps.put("user", dataSourceProperties.getUsername());
        dsProps.put("password", dataSourceProperties.getPassword());
        dsProps.put("prepStmtCacheSize", 250);
        dsProps.put("prepStmtCacheSqlLimit", 2048);
        dsProps.put("cachePrepStmts", Boolean.TRUE);
        dsProps.put("useServerPrepStmts", Boolean.TRUE);
        Properties configProps = new Properties();
        configProps.put("dataSourceClassName", dataSourceProperties.getDataSourceClassName());
        configProps.put("poolName", dataSourceProperties.getPoolName());
        configProps.put("maximumPoolSize", dataSourceProperties.getMaximumPoolSize());
        configProps.put("minimumIdle", dataSourceProperties.getMinimumIdle());
        configProps.put("connectionTimeout", dataSourceProperties.getConnectionTimeout());
        configProps.put("idleTimeout", dataSourceProperties.getIdleTimeout());
        configProps.put("dataSourceProperties", dsProps);
        HikariConfig hc = new HikariConfig(configProps);
        HikariDataSource ds = new HikariDataSource(hc);
        return ds;
    }
}
