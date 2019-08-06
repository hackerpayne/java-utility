package com.lingdonge.db.dynamic.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源切换
 * 根据当前线程来选择具体的数据源
 */
public class DBContextHolder {

    /**
     * 数据源的KEY
     */
    public static final String DATASOURCE_KEY = "DATASOURCE_KEY";

    /**
     * 数据源的URL
     */
    public static final String DATASOURCE_URL = "DATASOURCE_URL";

    /**
     * 数据源的驱动
     */
    public static final String DATASOURCE_DRIVER = "DATASOURCE_DRIVER";

    /**
     * 数据源的用户名
     */
    public static final String DATASOURCE_USERNAME = "DATASOURCE_USERNAME";

    /**
     * 数据源的密码
     */
    public static final String DATASOURCE_PASSWORD = "DATASOURCE_PASSWORD";

    private static final ThreadLocal<Map<String, Object>> contextHolder = new ThreadLocal<Map<String, Object>>();

    public static void setDBType(Map<String, Object> dataSourceConfigMap) {
        contextHolder.set(dataSourceConfigMap);
    }

    public static Map<String, Object> getDBType() {
        Map<String, Object> dataSourceConfigMap = contextHolder.get();
        if (dataSourceConfigMap == null) {
            dataSourceConfigMap = new HashMap<String, Object>();
        }
        return dataSourceConfigMap;
    }

    public static void clearDBType() {
        contextHolder.remove();
    }


    /**
     * 存放数据源id
     */
    public static List<String> dataSourceIds = new ArrayList<String>();

    /**
     * 判断当前数据源是否存在
     *
     * @param dataSourceId
     * @return
     */
    public static boolean isContainsDataSource(String dataSourceId) {
        return dataSourceIds.contains(dataSourceId);
    }
}
