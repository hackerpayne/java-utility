package com.lingdonge.db.dynamic.datasource;


import com.alibaba.druid.pool.DruidDataSource;
import com.lingdonge.db.configuration.properties.DruidProperties;
import com.lingdonge.db.util.DataSourceBuilder;
import com.lingdonge.spring.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * DruidDynamicDataSource只是简单地创建了一个基于Druid的数据池。
 * 可以直接继承创建一个AtomikosDynamicDataSource类，直接使用Atomikos实现分布式的事务管理
 */
@Slf4j
public class DruidDynamicDataSource extends AbstractDynamicDataSource<DruidDataSource> {

    public DruidDynamicDataSource() {
        log.info("加载[{}]类", this.getClass().getSimpleName());
    }

//    /**
//     * Druid配置文件
//     */
//    @Autowired
//    private DruidProperties dBProperties;

    /**
     * 创建Atomikos数据源，以实现分布式的事务操作
     *
     * @param driverClassName 数据库驱动名称
     * @param url             连接地址
     * @param username        用户名
     * @param password        密码
     * @return
     */
    @Override
    public DruidDataSource createDataSource(String driverClassName, String url,
                                            String username, String password) {

        DruidProperties dBProperties = SpringContextUtil.getBean(DruidProperties.class);

        DruidDataSource druidDataSource = DataSourceBuilder.createDruidDataSource(dBProperties);

        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setDriverClassName(driverClassName);

        return druidDataSource;
    }


}
