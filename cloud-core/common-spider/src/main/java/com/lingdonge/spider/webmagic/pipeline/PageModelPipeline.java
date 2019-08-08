package com.lingdonge.spider.webmagic.pipeline;

import com.lingdonge.spider.webmagic.Task;

/**
 * Implements PageModelPipeline to persistent your page bean.
 */
public interface PageModelPipeline<T> {

    void process(T t, Task task);

}
