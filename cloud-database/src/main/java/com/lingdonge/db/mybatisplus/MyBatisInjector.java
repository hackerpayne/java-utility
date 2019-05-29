package com.lingdonge.db.mybatisplus;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自定义全局操作，可以把自定义的SQL注入器统一到这里注入
 * 使用时：即可注入所有相关的功能
 * public ISqlInjector iSqlInjector() {
 * return new MyBatisInjector();
 * }
 */
public class MyBatisInjector extends AbstractSqlInjector {

    /**
     * 获取所有注入的方法集合
     *
     * @return
     */
    @Override
    public List<AbstractMethod> getMethodList() {
        return Stream.of(
                new SelectNewPageInjector()
        ).collect(Collectors.toList());
    }

}