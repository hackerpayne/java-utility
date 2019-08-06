//package com.kyle.database.dynamic.datasource;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.stat.DruidDataSourceStatManager;
//import DruidProperties;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.log4j.Logger;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//import javax.sql.DataSource;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.token.*;
//
///**
// * 动态数据源切换类
// * https://www.cnblogs.com/wsss/p/5475057.html
// * http://www.tpyyes.com/a/javaweb/2017/0829/192.html
// */
//public class DynamicDataSource extends AbstractRoutingDataSource {
//    private Logger log = Logger.getLogger(this.getClass());
//
//    /**
//     * 默认数据源，也就是主库
//     */
//    protected DataSource masterDataSource;
//
//    /**
//     * 数据源配置
//     */
//    private DruidProperties properties;
//
//    // 保存动态创建的数据源
//    private static final Map targetDataSource = new HashMap<>();
//
//    public DynamicDataSource() {
//
//    }
//
//    public DynamicDataSource(DruidProperties prop) {
//        this.properties = prop;
//        log.info("<<<<<<<<<<<<<<< 使用动态数据源 AbstractRoutingDataSource 进行加载 程序已经启动 >>>>>>>>>>>>>>>>>>");
//    }
//
//    @Override
//    protected String determineCurrentLookupKey() {
//
//        String dataSourceName = DBContextHolder.getDataSource();
//        if (StringUtils.isEmpty(dataSourceName) || dataSourceName.equals("dataSource")) {
//            // 默认的数据源名字
//            dataSourceName = "dataSource";
//        }
//        log.info("determineCurrentLookupKey使用数据源 : " + dataSourceName);
//        return dataSourceName;
//    }
//
//    @Override
//    protected DataSource determineTargetDataSource() {
//
//
//        // 根据数据库选择方案，拿到要访问的数据库
//        String dataSourceName = determineCurrentLookupKey();
//
//        log.info("determineTargetDataSource 使用数据源 : " + dataSourceName);
//
//        if ("dataSource".equals(dataSourceName)) {
//            // 访问默认主库
//            return masterDataSource;
//        }
//
//        // 根据数据库名字，从已创建的数据库中获取要访问的数据库
//        DataSource dataSource = (DataSource) targetDataSource.get(dataSourceName);
//        if (null == dataSource) {
//            log.info("---当前数据源：[" + dataSourceName + "]为空，准备创建数据源---");
//            // 从已创建的数据库中获取要访问的数据库，如果没有则创建一个
//            dataSource = this.selectDataSource(dataSourceName);
//        }
//        return dataSource;
//    }
//
//    /**
//     * 删除数据源
//     *
//     * @param datasourceid
//     * @return
//     */
//    public boolean delDatasources(String datasourceid) {
//        Map<Object, Object> dynamicTargetDataSources2 = this.targetDataSource;
//        if (dynamicTargetDataSources2.containsKey(datasourceid)) {
//            Set<DruidDataSource> druidDataSourceInstances = DruidDataSourceStatManager.getDruidDataSourceInstances();
//            for (DruidDataSource l : druidDataSourceInstances) {
//                if (datasourceid.equals(l.getName())) {
//                    System.out.println(l);
//                    dynamicTargetDataSources2.remove(datasourceid);
//                    DruidDataSourceStatManager.removeDataSource(l);
//                    setTargetDataSources(dynamicTargetDataSources2);// 将map赋值给父类的TargetDataSources
//                    super.afterPropertiesSet();// 将TargetDataSources中的连接信息放入resolvedDataSources管理
//                    return true;
//                }
//            }
//            return false;
//        } else {
//            return false;
//        }
//    }
//
//    /**
//     * 测试数据源连接是否有效
//     *
//     * @param key
//     * @param driveClass
//     * @param url
//     * @param username
//     * @param password
//     * @return
//     */
//    public boolean testDatasource(String key, String driveClass, String url, String username, String password) {
//        try {
//            Class.forName(driveClass);
//            DriverManager.getConnection(url, username, password);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//
//    /*public void setTargetDataSource(Map targetDataSource) {
//        this.targetDataSource = targetDataSource;
//        super.setTargetDataSources(this.targetDataSource);
//    }*/
//
//    /*public Map getTargetDataSource() {
//        return this.targetDataSource;
//    }*/
//
//    public void addTargetDataSource(String key, DataSource dataSource) {
//        this.targetDataSource.put(key, dataSource);
//        //setTargetDataSources(this.targetDataSource);
//    }
//
//
//    /**
//     * 该方法为同步方法，防止并发创建两个相同的数据库
//     * 使用双检锁的方式，防止并发
//     *
//     * @param dbType
//     * @return
//     */
//    private synchronized DataSource selectDataSource(String dbType) {
//        // 再次从数据库中获取，双检锁
//        DataSource obj = (DataSource) this.targetDataSource.get(dbType);
//        if (null != obj) {
//            return obj;
//        }
//        // 为空则创建数据库
//        DataSource dataSource = this.getDataSource(dbType);
//        if (null != dataSource) {
//            // 将新创建的数据库保存到map中
//            this.setDataSource(dbType, dataSource);
//            return dataSource;
//        } else {
//            log.error("创建数据源失败！");
//            return null;
//        }
//    }
//
//    /**
//     * 查询对应数据库的信息
//     *
//     * @param dbtype
//     * @return
//     */
//    private DataSource getDataSource(String dbtype) {
//        String oriType = DBContextHolder.getDataSource();
//
////         先切换回主库，可以不切主库。因为我们的租户信息不在主库。如果是租户连接字符串保存在主库，就需要连接到主库进行查询，然后拼接连接字符串
////        DBContextHolder.setDataSource("dataSource");
//
//        // 查询所需信息，比如租户的连接字符串等，可以动态设置
////        CenterDatebase datebase = centerDatebaseManager.getById(dbtype);
//
//        // 切换回目标库
////        DBContextHolder.setDataSource(oriType);
//
//        // 拼接连接字符串，创建DataSource
////        String url = "jdbc:sqlserver://" + datebase.getIp() + ":1433"
////                + ";DatabaseName=" + datebase.getDatabaseName();
////        BasicDataSource dataSource = createDataSource(url, datebase.getUserName(), datebase.getPassword());
////        return dataSource;
//
//        DataSource dataSource = createDruidDataSource(oriType);
//        return dataSource;
//    }
//
//    /**
//     * 根据租户ID创建数据源
//     *
//     * @param talentID
//     * @return
//     */
//    public DataSource createDruidDataSource(String talentID) {
//        DruidDataSource datasource = new DruidDataSource();
//
//        if (StringUtils.isEmpty(talentID))
//            datasource.setUrl(properties.getUrl());
//        else
//            datasource.setUrl(properties.getTenantUrl() + talentID + "?" + properties.getTenantPara());
//        datasource.setUsername(properties.getUsername());
//        datasource.setPassword(properties.getPassword());
//        datasource.setDriverClassName(properties.getDriverClassName());
//
//        //configuration
//        datasource.setInitialSize(properties.getInitialSize());
//        datasource.setMinIdle(properties.getMinIdle());
//        datasource.setMaxActive(properties.getMaxActive());
//        datasource.setMaxWait(properties.getMaxWait());
//        datasource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
//        datasource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
//        datasource.setValidationQuery(properties.getValidationQuery());
//        datasource.setTestWhileIdle(properties.isTestWhileIdle());
//        datasource.setTestOnBorrow(properties.isTestOnBorrow());
//        datasource.setTestOnReturn(properties.isTestOnReturn());
//        datasource.setPoolPreparedStatements(properties.isPoolPreparedStatements());
//
//        List<String> listInitSQL = new ArrayList<>();
//        // 不加utf8mb4的话，在插入表情符号的时候，会报错，改了这里还需要把表的含表情的字段改为utf8mb4的字段格式才可以，否则依然会报错。
//        //在连接字符串里面，不需要设置成utf8mb4，保留utf8就行了
//        listInitSQL.add("set names utf8mb4;");
//        datasource.setConnectionInitSqls(listInitSQL);
//
//        datasource.setMaxPoolPreparedStatementPerConnectionSize(properties.getMaxPoolPreparedStatementPerConnectionSize());
//        try {
//            datasource.setFilters(properties.getFilters());
//        } catch (SQLException e) {
//            logger.error("druid configuration initialization filter", e);
//        }
//        datasource.setConnectionProperties(properties.getConnectionProperties());
//        return datasource;
//    }
//
//
//    /**
//     * 设置数据源
//     *
//     * @param type
//     * @param dataSource
//     */
//    public void setDataSource(String type, DataSource dataSource) {
//        this.addTargetDataSource(type, dataSource);
//        DBContextHolder.setDataSource(type);
//    }
//
///*    @Override
//    public void setTargetDataSources(Map targetDataSources) {
//        super.setTargetDataSources(targetDataSources);
//        // 重点：通知container容器数据源发生了变化
//        afterPropertiesSet();
//    }*/
//
//    public void setDefaultTargetDataSource(DataSource defaultTargetDataSource) {
//        super.setDefaultTargetDataSource(defaultTargetDataSource);
//        this.masterDataSource = defaultTargetDataSource;
//    }
//
//    /**
//     * 该方法重写为空，因为AbstractRoutingDataSource类中会通过此方法将，targetDataSources变量中保存的数据源交给resolvedDefaultDataSource变量
//     * 在本方案中动态创建的数据源保存在了本类的targetDataSource变量中。如果不重写该方法为空，会因为targetDataSources变量为空报错
//     * 如果仍然想要使用AbstractRoutingDataSource类中的变量保存数据源，则需要在每次数据源变更时，调用此方法来为resolvedDefaultDataSource变量更新
//     */
//    @Override
//    public void afterPropertiesSet() {
//    }
//
//    public DataSource getMasterDataSource() {
//        return masterDataSource;
//    }
//
//    public void setMasterDataSource(DataSource masterDataSource) {
//        this.masterDataSource = masterDataSource;
//    }
//}