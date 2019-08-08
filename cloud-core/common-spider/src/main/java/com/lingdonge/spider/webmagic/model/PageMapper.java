package com.lingdonge.spider.webmagic.model;

import com.lingdonge.spider.webmagic.Page;

import java.util.List;

/**
 *
 */
public class PageMapper<T> {

    private Class<T> clazz;

    private PageModelExtractor pageModelExtractor;

    public PageMapper(Class<T> clazz) {
        this.clazz = clazz;
        this.pageModelExtractor = PageModelExtractor.create(clazz);
    }

    public T get(Page page) {
        return (T) pageModelExtractor.process(page);
    }

    public List<T> getAll(Page page) {
        return (List<T>) pageModelExtractor.process(page);
    }
}
