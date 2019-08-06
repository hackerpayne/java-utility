package com.lingdonge.http.proxy;

import com.lingdonge.core.bean.common.ModelProxy;
import com.lingdonge.http.HttpResult;
import com.lingdonge.http.HttpSetting;

/**
 * 代理IP提供的接口
 */
public interface ProxyProvider {

    /**
     * 使用完成之后，退还IP代理
     *
     * @param proxy
     * @param page
     * @param httpSetting
     */
    void returnProxy(ModelProxy proxy, HttpResult page, HttpSetting httpSetting);

    /**
     * 获取一条代理
     *
     * @param httpSetting
     * @return
     */
    ModelProxy getProxy(HttpSetting httpSetting);

}
