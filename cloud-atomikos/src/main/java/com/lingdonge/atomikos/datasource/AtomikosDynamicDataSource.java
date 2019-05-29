package com.lingdonge.atomikos.datasource;


import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.lingdonge.atomikos.configuration.properties.AtomikosDataSourceProperties;
import com.kyle.database.configuration.properties.DruidProperties;
import com.kyle.database.dynamic.datasource.AbstractDynamicDataSource;
import com.kyle.database.util.DataSourceBuilder;
import com.kyle.utility.dates.SystemClock;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 基于atomikos的动态数据源类，其实我还有一个DruidDynamicDataSource类，这里没有给出，内容大部分一样，只是createDataSource的实现内容不一样，DruidDynamicDataSource只是简单地创建了一个基于Druid的数据池，而AtomikosDynamicDataSource是创建的一个可加入atomikos分布式事务的数据池。
 */
@Slf4j
public class AtomikosDynamicDataSource extends AbstractDynamicDataSource<AtomikosDataSourceBean> {

    public AtomikosDynamicDataSource() {
        log.info("加载[{}]类", this.getClass().getSimpleName());
    }

    /**
     * Druid配置文件
     */
    @Resource
    private DruidProperties dBProperties;

    @Resource
    private AtomikosDataSourceProperties atomikosProperties;

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
    public AtomikosDataSourceBean createDataSource(String driverClassName, String url, String username, String password) {

        // 创建出默认的数据源
        DruidXADataSource ds = DataSourceBuilder.createDruidXADataSource(dBProperties);

        // 修改配置信息
        ds.setName(String.valueOf(SystemClock.now()));
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driverClassName);

        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setXaDataSource(ds);

        // 设置数据源的唯一名称，不允许重复
        atomikosDataSourceBean.setUniqueResourceName(String.valueOf(SystemClock.now()));
        atomikosDataSourceBean.setMaxPoolSize(atomikosProperties.getMaxPoolSize());
        atomikosDataSourceBean.setMinPoolSize(atomikosProperties.getMinPoolSize());
        atomikosDataSourceBean.setBorrowConnectionTimeout(atomikosProperties.getBorrowConnectionTimeout());
        //        atomikosDataSourceBean.getXaProperties().setProperty("", arg1);
        //        atomikosDataSourceBean.getXaProperties().setProperty("", arg1);
        //        atomikosDataSourceBean.getXaProperties().setProperty("", arg1);
        //        atomikosDataSourceBean.getXaProperties().setProperty("", arg1);
        atomikosDataSourceBean.setMaxLifetime(0);

        return atomikosDataSourceBean;
    }

}
