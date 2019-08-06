package com.lingdonge.quartz.configuration;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * 解决Job中注入Spring Bean为null的问题
 */
@Component("quartzJobFactory")
public class QuartzJobFactory extends AdaptableJobFactory {

    /**
     * AutowireCapableBeanFactory接口是BeanFactory的子类
     * 可以连接和填充那些生命周期不被Spring管理的已存在的bean实例
     */
    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;

//    public QuartzJobFactory(AutowireCapableBeanFactory factory) {
//        this.capableBeanFactory = factory;
//    }

    /**
     * 创建Job实例
     *
     * @param bundle
     * @return
     * @throws Exception
     */
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        //调用父类的方法
        Object jobInstance = super.createJobInstance(bundle);

        //进行注入,这属于Spring的技术,不清楚的可以查看Spring的API.
        capableBeanFactory.autowireBean(jobInstance);

        return jobInstance;
    }
}