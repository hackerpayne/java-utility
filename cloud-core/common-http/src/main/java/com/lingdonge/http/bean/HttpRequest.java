package com.lingdonge.http.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 抓取请求类
 */
public class HttpRequest implements Serializable {

    private static final long serialVersionUID = 2062192774891352043L;

    /**
     * 保存重试信息的字符串标记
     */
    public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";

    /**
     *
     */
    private String url;

    /**
     * 请求方式
     */
    private String method;

    /**
     * HttpBody原始数据
     */
    private HttpRequestBody requestBody;

    /**
     * 保存额外的数据
     */
    private Map<String, Object> extras;

    /**
     * 添加的Cookie列表
     */
    private Map<String, String> cookies = new HashMap<String, String>();

    /**
     * 添加的头部信息列表
     */
    private Map<String, String> headers = new HashMap<String, String>();

    /**
     * 如果为True将不会解析内容为String格式
     */
    private boolean binaryContent = false;

    /**
     * 单独的字符串编码
     */
    private String charset;

    public boolean isReturnHeader() {
        return returnHeader;
    }

    public void setReturnHeader(boolean returnHeader) {
        this.returnHeader = returnHeader;
    }

    /**
     * 是否返回Header信息，默认是不需要的
     */
    private boolean returnHeader = false;

    public HttpRequest() {
    }

    public HttpRequest(String url) {
        this.url = url;
    }

    public HttpRequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(HttpRequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public Object getExtra(String key) {
        if (extras == null) {
            return null;
        }
        return extras.get(key);
    }

    /**
     * 获取Extra里面的额外的数据
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public Object getExtra(String key, Object defaultValue) {
        if (extras == null) {
            return defaultValue;
        }
        Object obj = extras.get(key);
        if (obj == null && defaultValue != null) {
            return defaultValue;
        }
        return obj;
    }

    public HttpRequest putExtra(String key, Object value) {
        if (extras == null) {
            extras = new HashMap<String, Object>();
        }
        extras.put(key, value);
        return this;
    }

    public String getUrl() {
        return url;
    }

    public HttpRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public HttpRequest setExtras(Map<String, Object> extras) {
        this.extras = extras;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public HttpRequest setMethod(String method) {
        this.method = method;
        return this;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (method != null ? method.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HttpRequest request = (HttpRequest) o;

        if (url != null ? !url.equals(request.url) : request.url != null) return false;
        return method != null ? method.equals(request.method) : request.method == null;
    }

    public HttpRequest addCookie(String name, String value) {
        cookies.put(name, value);
        return this;
    }

    public HttpRequest addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean isBinaryContent() {
        return binaryContent;
    }

    public HttpRequest setBinaryContent(boolean binaryContent) {
        this.binaryContent = binaryContent;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public HttpRequest setCharset(String charset) {
        this.charset = charset;
        return this;
    }

}
