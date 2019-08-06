package com.lingdonge.http.proxy;

import com.lingdonge.core.bean.common.ModelProxy;
import com.lingdonge.http.HttpResult;
import com.lingdonge.http.HttpSetting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询使用代理配置
 */
public class SimpleProxyProvider implements ProxyProvider {

    private final List<ModelProxy> proxies;

    private final AtomicInteger pointer;

    /**
     * 构造传入代理列表
     *
     * @param proxies
     */
    public SimpleProxyProvider(List<ModelProxy> proxies) {
        this(proxies, new AtomicInteger(-1));
    }

    private SimpleProxyProvider(List<ModelProxy> proxies, AtomicInteger pointer) {
        this.proxies = proxies;
        this.pointer = pointer;
    }

    /**
     * 手工加载代理列表进来
     *
     * @param proxies
     * @return
     */
    public static SimpleProxyProvider from(ModelProxy... proxies) {
        List<ModelProxy> proxiesTemp = new ArrayList<ModelProxy>(proxies.length);
        for (ModelProxy proxy : proxies) {
            proxiesTemp.add(proxy);
        }
        return new SimpleProxyProvider(Collections.unmodifiableList(proxiesTemp));
    }

    @Override
    public void returnProxy(ModelProxy proxy, HttpResult page, HttpSetting task) {
        //Donothing
    }

    /**
     * 随机获取一条代理
     *
     * @param task
     * @return
     */
    @Override
    public ModelProxy getProxy(HttpSetting task) {
        return proxies.get(incrForLoop());
    }

    /**
     * @return
     */
    private int incrForLoop() {
        int p = pointer.incrementAndGet();
        int size = proxies.size();
        if (p < size) {
            return p;
        }
        while (!pointer.compareAndSet(p, p % size)) {
            p = pointer.get();
        }
        return p % size;
    }
}
