package com.lingdonge.spider.webmagic.proxy;

import com.lingdonge.core.bean.common.ModelProxy;
import com.lingdonge.spider.webmagic.Page;
import com.lingdonge.spider.webmagic.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple ProxyProvider. Provide proxy as round-robin without heartbeat and error check. It can be used when all proxies are stable.
 *
 * @author code4crafter@gmail.com
 * Date: 17/4/16
 * Time: 10:18
 * @since 0.7.0
 */
public class SimpleProxyProvider implements ProxyProvider {

    private final List<ModelProxy> proxies;

    private final AtomicInteger pointer;

    public SimpleProxyProvider(List<ModelProxy> proxies) {
        this(proxies, new AtomicInteger(-1));
    }

    private SimpleProxyProvider(List<ModelProxy> proxies, AtomicInteger pointer) {
        this.proxies = proxies;
        this.pointer = pointer;
    }

    public static SimpleProxyProvider from(ModelProxy... proxies) {
        List<ModelProxy> proxiesTemp = new ArrayList<ModelProxy>(proxies.length);
        for (ModelProxy proxy : proxies) {
            proxiesTemp.add(proxy);
        }
        return new SimpleProxyProvider(Collections.unmodifiableList(proxiesTemp));
    }

    @Override
    public void returnProxy(ModelProxy proxy, Page page, Task task) {
        //Donothing
    }

    @Override
    public ModelProxy getProxy(Task task) {
        return proxies.get(incrForLoop());
    }

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
