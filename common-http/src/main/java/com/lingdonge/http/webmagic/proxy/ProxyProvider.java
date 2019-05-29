package com.lingdonge.http.webmagic.proxy;

import com.lindonge.core.model.ModelProxy;
import com.lingdonge.http.webmagic.Page;
import com.lingdonge.http.webmagic.Task;

/**
 * ModelProxy provider. <br>
 *
 * @since 0.7.0
 */
public interface ProxyProvider {

    /**
     * Return proxy to Provider when complete a download.
     *
     * @param proxy the proxy configuration contains host,port and identify info
     * @param page  the download result
     * @param task  the download task
     */
    void returnProxy(ModelProxy proxy, Page page, Task task);

    /**
     * Get a proxy for task by some strategy.
     *
     * @param task the download task
     * @return proxy
     */
    ModelProxy getProxy(Task task);

}
