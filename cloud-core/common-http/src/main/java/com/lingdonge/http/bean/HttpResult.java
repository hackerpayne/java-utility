package com.lingdonge.http.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.lingdonge.core.http.HttpConstant;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 抓取结果
 * 保存抓取的结果信息
 */
@Data
public class HttpResult {

    /**
     * 获取到的源内容
     */
    private String rawText;

    /**
     * URL当前地址
     */
    private String url;

    /**
     * 返回的头信息列表
     */
    private Map<String, List<String>> headers;

    /**
     * 返回状态码
     */
    private int statusCode = HttpConstant.StatusCode.CODE_200;

    /**
     * 是否成功下载
     */
    private boolean downloadSuccess = true;

    /**
     * 源数据的流
     */
    @JSONField(serialize = false, deserialize = false)
    private byte[] bytes;

    /**
     * 编码信息
     */
    private String charset;

    /**
     * 跳转URL，用于取出最后一次跳转的URL出来
     */
    private String locationUrl;


}
