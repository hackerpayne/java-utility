package com.lindonge.core.model;

import com.google.common.collect.Maps;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * URL实体类,解析URL的链接,请求字符串,参数等
 * Created by Kyle on 16/8/29.
 */
@Data
public class ModelUrl implements Serializable {

    private static final long serialVersionUID = -1;

    private String url;
    private String cleanUrl;
    private String queryStr;
    private String fragment;
    private Map<String, String> queryMap = Maps.newHashMap();

    /**
     * 构造函数
     *
     * @param url URL地址
     */
    public ModelUrl(String url) {
        this.url = url;
    }

}
