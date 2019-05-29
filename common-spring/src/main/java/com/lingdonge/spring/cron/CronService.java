package com.lingdonge.spring.cron;

/**
 * 可以继承CronService实现数据库或者别的地方控制定时器和定时任务
 */
public interface CronService {

    /**
     * 获取Cron表达式
     *
     * @return
     */
    String getCron();

    String getBeanName();
}
