package com.lingdonge.http.webmagic.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 采集的字段列表
 */
@Data
public class SpiderFields implements Serializable {

    private static final long serialVersionUID = -1;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段别名
     */
    private String alias;

    /**
     * 抽取表达式
     */
    private String selector;
    /**
     * 使用的匹配方式

     */
    private String selectorType;

}
