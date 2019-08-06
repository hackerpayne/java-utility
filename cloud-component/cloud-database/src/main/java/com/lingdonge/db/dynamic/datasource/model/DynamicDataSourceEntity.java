package com.lingdonge.db.dynamic.datasource.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 数据源配置信息实体类，
 */
@Data
public class DynamicDataSourceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据源的标记Key
     */
    private String datasourceKey;

    /**
     * Driver连接器
     */
    private String driver;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * URL连接字符串
     */
    private String url;
}
