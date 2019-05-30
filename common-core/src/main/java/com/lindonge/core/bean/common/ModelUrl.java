package com.lindonge.core.bean.common;

import com.google.common.collect.Maps;
import com.lindonge.core.bean.base.BaseEntity;
import lombok.Data;

import java.util.Map;

/**
 * URL实体类,解析URL的链接,请求字符串,参数等
 * Created by Kyle on 16/8/29.
 */
@Data
public class ModelUrl extends BaseEntity {

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
