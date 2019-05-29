package com.lingdonge.db.dynamic.datasource;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.lingdonge.db.util.DataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Set;

/**
 * 动态数据源父类
 * 该类继承AbstractRoutingDataSource，并重写determineCurrentLookupKey方法，用于确定使用哪套数据源。该类又实现了ApplicationContextAware，在Spring容器初始化的时候，会执行setApplicationContext方法，将Spring容器的上下文对象注入到属性ctx中，然后就可以使用ctx.getBean(name)来获取Spring容器中的对象。
 * 代码参考：https://my.oschina.net/simpleton/blog/916108
 * <p>
 * 可以在代码中动态创建数据源并注入
 * https://www.cnblogs.com/wsss/p/5475057.html
 * http://www.tpyyes.com/a/javaweb/2017/0829/192.html
 * https://blog.csdn.net/fangdengfu123/article/details/70139644
 * https://blog.csdn.net/u012881904/article/details/77449710
 */
@Slf4j
public abstract class AbstractDynamicDataSource<T extends DataSource> extends AbstractRoutingDataSource implements ApplicationContextAware {

    /**
     * 数据源KEY-VALUE键值对
     */
    private Map<Object, Object> targetDataSources;

    /**
     * spring容器上下文
     */
    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        log.info("初始化[AbstractDynamicDataSource]类，并注入容器");
        ctx = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return ctx;
    }

    public static Object getBean(String name) {
        return ctx.getBean(name);
    }

    /**
     * @param targetDataSources the targetDataSources to set
     */
    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.targetDataSources = targetDataSources;
        super.setTargetDataSources(targetDataSources);
        // afterPropertiesSet()方法调用时用来将targetDataSources的属性写入resolvedDataSources中的
        super.afterPropertiesSet();
    }

    /**
     * @return the targetDataSources
     */
    public Map<Object, Object> getTargetDataSources() {
        return this.targetDataSources;
    }

    /**
     * 创建数据源
     *
     * @param driverClassName 数据库驱动名称
     * @param url             连接地址
     * @param username        用户名
     * @param password        密码
     * @return
     */
    public abstract T createDataSource(String driverClassName, String url, String username, String password);

    /**
     * 设置系统当前使用的数据源
     * <p>数据源为空或者为0时，自动切换至默认数据源，即在配置文件中定义的默认数据源
     *
     * @see org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource#determineCurrentLookupKey()
     */
    @Override
    protected Object determineCurrentLookupKey() {

        Map<String, Object> configMap = DBContextHolder.getDBType();
        log.info("【当前数据源将切换为：{}】", configMap.get(DBContextHolder.DATASOURCE_KEY));

        if (MapUtils.isEmpty(configMap)) {
            // 使用默认数据源
            return DataSourceBuilder.DEFAULT_DATASOURCE_KEY;
//            throw new IllegalArgumentException("没有指定数据源");
        }

        this.verifyAndInitDataSource();// 判断数据源是否需要初始化

        return configMap.get(DBContextHolder.DATASOURCE_KEY);
    }


//    /**
//     *
//     * @return
//     */
//    @Override
//    protected DataSource determineTargetDataSource() {
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

//    /**
//     * 该方法为同步方法，防止并发创建两个相同的数据库
//     * 使用双检锁的方式，防止并发
//     *
//     * @param dbType
//     * @return
//     */
//    private synchronized DataSource selectDataSource(String dbType) {
//        // 再次从数据库中获取，双检锁
//        DataSource obj = (DataSource) targetDataSource.get(dbType);
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

    /**
     * 判断数据源是否需要初始化，如果是没有的数据源，会动态进行增加
     */
    private void verifyAndInitDataSource() {
        Map<String, Object> configMap = DBContextHolder.getDBType();
        Object obj = this.targetDataSources.get(configMap.get(DBContextHolder.DATASOURCE_KEY));
        if (obj != null) {
            return;
        }
        log.info("初始化数据源【{}】", configMap.get(DBContextHolder.DATASOURCE_KEY));

        // 如果没有数据源，在这里，进行创建
        T datasource = this.createDataSource(configMap.get(DBContextHolder.DATASOURCE_DRIVER).toString(), configMap.get(DBContextHolder.DATASOURCE_URL).toString(),
                configMap.get(DBContextHolder.DATASOURCE_USERNAME).toString(),
                configMap.get(DBContextHolder.DATASOURCE_PASSWORD).toString());

        // 将创建好的数据源添加到List里面
        this.addTargetDataSource(configMap.get(DBContextHolder.DATASOURCE_KEY).toString(), datasource);
    }

    /**
     * 往数据源key-value键值对集合添加新的数据源
     *
     * @param key        新的数据源键
     * @param dataSource 新的数据源
     */
    public void addTargetDataSource(String key, T dataSource) {
        this.targetDataSources.put(key, dataSource);
        super.setTargetDataSources(this.targetDataSources);
        // afterPropertiesSet()方法调用时用来将targetDataSources的属性写入resolvedDataSources中的
        super.afterPropertiesSet();
    }

    /**
     * 该方法重写为空，因为AbstractRoutingDataSource类中会通过此方法将，targetDataSources变量中保存的数据源交给resolvedDefaultDataSource变量
     * 在本方案中动态创建的数据源保存在了本类的targetDataSource变量中。如果不重写该方法为空，会因为targetDataSources变量为空报错
     * 如果仍然想要使用AbstractRoutingDataSource类中的变量保存数据源，则需要在每次数据源变更时，调用此方法来为resolvedDefaultDataSource变量更新
     */
    @Override
    public void afterPropertiesSet() {
    }

    /**
     * 测试数据源连接是否有效
     *
     * @param key
     * @param driveClass
     * @param url
     * @param username
     * @param password
     * @return
     */
    public boolean testDatasource(String key, String driveClass, String url, String username, String password) {
        try {
            Class.forName(driveClass);
            DriverManager.getConnection(url, username, password);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除数据源
     *
     * @param datasourceid
     * @return
     */
    public boolean delDatasources(String datasourceid) {
        Map<Object, Object> dynamicTargetDataSources2 = this.targetDataSources;
        if (dynamicTargetDataSources2.containsKey(datasourceid)) {
            Set<DruidDataSource> druidDataSourceInstances = DruidDataSourceStatManager.getDruidDataSourceInstances();
            for (DruidDataSource l : druidDataSourceInstances) {
                if (datasourceid.equals(l.getName())) {
                    System.out.println(l);
                    dynamicTargetDataSources2.remove(datasourceid);
                    DruidDataSourceStatManager.removeDataSource(l);
                    setTargetDataSources(dynamicTargetDataSources2);// 将map赋值给父类的TargetDataSources
                    super.afterPropertiesSet();// 将TargetDataSources中的连接信息放入resolvedDataSources管理
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

}
