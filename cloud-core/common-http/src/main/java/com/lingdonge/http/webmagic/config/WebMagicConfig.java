package com.lingdonge.http.webmagic.config;

import lombok.Data;

import java.io.Serializable;

/**
 * WebMagic的采集配置设置
 */
@Data
public class WebMagicConfig implements Serializable {
    private static final long serialVersionUID = 2062192774891352043L;

    private int threads = 5;
    private boolean exitWhenComplete = false;

}