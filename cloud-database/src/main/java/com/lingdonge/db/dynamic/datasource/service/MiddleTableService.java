package com.lingdonge.db.dynamic.datasource.service;


import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Lists;
import com.lingdonge.db.configuration.properties.DruidProperties;
import com.lingdonge.db.dynamic.datasource.DBContextHolder;
import com.lingdonge.db.dynamic.datasource.DruidDynamicDataSource;
import com.lingdonge.db.dynamic.datasource.model.DynamicDataSourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 中间表服务类，可以根据租户ID或者用户信息，或者任何数据，动态创建添加数据源
 */
@Service
@Slf4j
public class MiddleTableService {

    /**
     * 动态数据源，可以是任何动态的数据源
     */
    @Resource
    @Lazy
    private DruidDynamicDataSource druidDynamicDataSource;

    /**
     * 配置文件加载进行方便使用
     */
    @Resource
    @Lazy
    private DruidProperties druidProperties;

    /**
     * 获取所有数据源信息
     * 可以自行使用，比如从配置文件读多个数据源，从数据库读多个数据源，均可以
     *
     * @return
     */
    public List<DynamicDataSourceEntity> getAllDataSouces() {

        List<DynamicDataSourceEntity> listDataSources = Lists.newArrayList();

        DynamicDataSourceEntity dynamicDataSourceEntity = new DynamicDataSourceEntity();
        dynamicDataSourceEntity.setDatasourceKey("defaultDataSource");
        dynamicDataSourceEntity.setDriver(druidProperties.getDriverClassName());
        dynamicDataSourceEntity.setUsername(druidProperties.getUsername());
        dynamicDataSourceEntity.setPassword(druidProperties.getPassword());
        dynamicDataSourceEntity.setUrl(druidProperties.getUrl());

        listDataSources.add(dynamicDataSourceEntity);
        return listDataSources;
    }

    /**
     * 获取租户ID对应的正确的连接信息
     *
     * @param tanentId
     * @return
     */
    public DynamicDataSourceEntity getDataSourceByTanentId(String tanentId) {

        if (StringUtils.isEmpty(tanentId)) {
            tanentId = "";
        } else {
            tanentId = tanentId.trim();
        }

        DynamicDataSourceEntity dynamicDataSourceEntity = new DynamicDataSourceEntity();
        dynamicDataSourceEntity.setDatasourceKey(tanentId);
        dynamicDataSourceEntity.setDriver(druidProperties.getDriverClassName());
        dynamicDataSourceEntity.setUsername(druidProperties.getUsername());
        dynamicDataSourceEntity.setPassword(druidProperties.getPassword());
        dynamicDataSourceEntity.setUrl(druidProperties.getTenantUrl() + tanentId + "?" + druidProperties.getTenantPara());
        return dynamicDataSourceEntity;
    }

    /**
     * 服务启动时，创建所有数据源
     * 可以添加  ApplicationListener<ApplicationEvent> 在Spring加载时，全部把数据源准备好。
     */
    public synchronized void init() {
        log.info("【初始化数据源 】");

        // 清空所有数据源
        druidDynamicDataSource.getTargetDataSources().clear();

        // 读取自己的数据源
        List<DynamicDataSourceEntity> listDataSources = getAllDataSouces();
        if (CollectionUtils.isEmpty(listDataSources)) {
            log.info("【无数据源配置信息，终止数据源初始化任务】");
            return;
        }

        for (DynamicDataSourceEntity dynamicDataSource : listDataSources) {
            // 判断数据源是否已经被初始化
            if (druidDynamicDataSource.getTargetDataSources().containsKey(dynamicDataSource.getDatasourceKey())) {
                // 已经初始化
                continue;
            }

            // 创建数据源
            DruidDataSource dataSource = druidDynamicDataSource.createDataSource(dynamicDataSource.getDriver(), dynamicDataSource.getUrl(), dynamicDataSource.getUsername(), dynamicDataSource.getPassword());

            // 添加到targetDataSource中，缓存起来
            druidDynamicDataSource.addTargetDataSource(dynamicDataSource.getDatasourceKey(), dataSource);
        }
    }

    /**
     * 数据源控制开关，用于指定数据源
     * 比如使用租户ID，自动进行切换
     * 可以实现更多的方法进行使用
     *
     * @param tanentId 租户ID
     */
    public void dataSourceSwitch(String tanentId) {

        DynamicDataSourceEntity entityDataSource = getDataSourceByTanentId(tanentId);
        if (entityDataSource == null) {
            log.error("根据接口唯一码未获取到中间表配置信息");
            return;
        }

        if (StringUtils.isBlank(entityDataSource.getDriver()) && StringUtils.isBlank(entityDataSource.getUrl()) && StringUtils.isBlank(entityDataSource.getUsername())) {
            throw new IllegalArgumentException(String.format("接口【%s】未配置中间表信息，无法切换数据源", tanentId));
        }

        // 动态设置和添加数据源到线程隔离池中
        Map<String, Object> dataSourceConfigMap = new HashMap<String, Object>();
        dataSourceConfigMap.put(DBContextHolder.DATASOURCE_KEY, entityDataSource.getDatasourceKey());
        dataSourceConfigMap.put(DBContextHolder.DATASOURCE_DRIVER, entityDataSource.getDriver());
        dataSourceConfigMap.put(DBContextHolder.DATASOURCE_URL, entityDataSource.getUrl());
        dataSourceConfigMap.put(DBContextHolder.DATASOURCE_USERNAME, entityDataSource.getUsername());
        dataSourceConfigMap.put(DBContextHolder.DATASOURCE_PASSWORD, entityDataSource.getPassword());
        log.info("【指定数据源： {}:{}】", dataSourceConfigMap.get(DBContextHolder.DATASOURCE_KEY), entityDataSource.getUrl());
        DBContextHolder.setDBType(dataSourceConfigMap);

    }
}