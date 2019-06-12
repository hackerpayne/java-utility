package com.lingdonge.core.http;

import com.lingdonge.core.bean.common.ModelUrl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * URL解析器,解析出URL,请求字符串和片段等信息
 * Created by Kyle on 16/8/23.
 */
@Slf4j
public class URLParser {

    private URI uri;

    private String defaultCharset = "utf-8";

    /**
     * 构造函数
     *
     * @param url URL地址
     */
    public URLParser(String url) {
        this(url, "");
    }

    /**
     * 构造函数
     *
     * @param url     URL地址
     * @param charset 编码
     */
    public URLParser(String url, String charset) {

        try {

            if (StringUtils.isNoneEmpty(charset)) {
                this.defaultCharset = charset;
            }

            this.uri = new URI(URLDecoder.decode(url, this.defaultCharset));

        } catch (Exception e) {
            this.uri = null;
            log.error("URLParser 异常", e);
        }
    }

    /**
     * 获取不带请求字符串和fragment的干净URL
     *
     * @return
     */
    public String getCleanUrl() {
        try {

            return new URI(this.uri.getScheme(), this.uri.getAuthority(), this.uri.getPath(),
                    null, // 忽略查询字符串
                    null // 忽略fragement片段
            ).toString();

        } catch (URISyntaxException e) {
            log.error("getCleanUrl trigger error ", e);
        }

        return "";
    }

    /**
     * 获取URL请求字符串信息
     *
     * @return
     */
    public String getQueryStr() {
        return this.uri.getQuery();
    }

    /**
     * 获取URL请求片段信息Fragment
     *
     * @return
     */
    public String getFragement() {
        return this.uri.getFragment();
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @return url请求参数部分
     */
    public Map<String, String> parseQuery() {
        return UrlUtils.getUrlPara(this.getQueryStr());
    }

    /**
     * URL解析到Model实体类里面
     *
     * @return
     */
    public ModelUrl parseUrlModel() {
        ModelUrl model = new ModelUrl(this.uri.toString());
        model.setCleanUrl(this.getCleanUrl());
        model.setQueryStr(this.getQueryStr());
        model.setFragment(this.getFragement());
        model.setQueryMap(this.parseQuery());
        return model;
    }

}
