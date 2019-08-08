package com.lingdonge.spider.webmagic.config;

import lombok.Data;

/**
 * MySQL配置
 */
@Data
public class MysqlConfig {

    private String url;
    private String username;
    private String password;

    private String driverClassName = "com.mysql.jdbc.Driver";

    private int maxActive = 50;

    private int maxIdle = 5;

    private int minIdle = 0;
}
