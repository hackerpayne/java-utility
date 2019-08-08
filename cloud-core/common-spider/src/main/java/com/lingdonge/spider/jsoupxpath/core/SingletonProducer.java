package com.lingdonge.spider.jsoupxpath.core;

/**
 * @author: github.com/zhegexiaohuozi [seimimaster@gmail.com]
 * Date: 14-3-16
 */
public class SingletonProducer {
    private static SingletonProducer producer = new SingletonProducer();
    private AxisSelector axisSelector = new AxisSelector();
    private Functions functions =new Functions();

    public static SingletonProducer getInstance() {
        return producer;
    }

    public AxisSelector getAxisSelector() {
        return axisSelector;
    }

    public Functions getFunctions() {
        return functions;
    }

    private SingletonProducer() {
    }
}
