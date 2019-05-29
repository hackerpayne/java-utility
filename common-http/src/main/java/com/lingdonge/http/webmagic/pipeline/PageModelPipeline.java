package com.lingdonge.http.webmagic.pipeline;

import com.lingdonge.http.webmagic.Task;

/**
 * Implements PageModelPipeline to persistent your page bean.
 */
public interface PageModelPipeline<T> {

    void process(T t, Task task);

}
