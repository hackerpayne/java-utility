package com.lingdonge.http.webmagic.config;

import lombok.Data;

/**
 * 云代理配置文件
 */
@Data
public class CloudProxyConfig {

    private String host = "127.0.0.1";
    private String key;
    private String secret;
    private int port = 9020;

}
