package com.lingdonge.spider.webmagic.config;

import com.lingdonge.core.bean.common.ModelProxy;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 爬虫的全局配置文件
 */
@Data
public class SpiderConfig implements Serializable {

    private static final long serialVersionUID = 2062192774891352043L;

    /**
     * 添加的Domain名称，必填项，多个逗号分割，满足此Domain的链接才会记录下来
     */
    private String dommains;

    /**
     * 入口URL列表
     */
    private String startUrls;

    /**
     * 抓取间隔
     */
    private Integer interval;

    /**
     * 使用的UA
     */
    private String userAgent;

    /**
     * 使用的编码
     */
    private String encode;

    /**
     * 请求超时时间
     */
    private Integer timeout;

    /**
     * 失败重试次数
     */
    private Integer tryTimes;

    /**
     * 附加的Header信息
     */
    private Map<String, String> appendHeader;

    /**
     * 是否开启JS
     */
    private boolean enableJS;

    /**
     * 设置Proxy的代理信息
     */
    private ModelProxy proxy;

    private List<SpiderFields> listFields;

}
