package com.lingdonge.spring.bean.request;

import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 */
@Getter
@Setter
public class RequestMethodItem implements Comparable<RequestMethodItem> {

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求参数
     */
    private String requestMethod;

    /**
     * 控制器
     */
    private String controller;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 类名
     */
    private String className;

    /**
     * 完整的请求方法
     */
    private String methodFull;

    /**
     * 返回值类型，为空一般是Form，如果是Json会显示Json值
     */
    private String responseType;

    /**
     * 参数明细
     */
    private List<RequestMethodParameter> parameters;

    /**
     *
     */
    private ApiOperation apiOperation;

    @Override
    public int compareTo(RequestMethodItem o) {
        return this.getPath().compareTo(o.getPath());
    }
}
