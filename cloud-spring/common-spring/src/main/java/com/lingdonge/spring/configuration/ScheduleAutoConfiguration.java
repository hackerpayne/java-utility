package com.lingdonge.spring.configuration;

import com.lingdonge.spring.configuration.properties.TaskThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.Resource;

/**
 * 自定义定时任务,系统自带定时任务的坑点：
 * 1、单线程，需要排队执行，耗时任务，极可能会造成执行时间无法按Cron的情况
 * 解决办法就是使用本方法的线程池，但是
 * 1、超出线程容量依然会造成排队，任务不执行。
 *
 * 最终解决方案：
 * 1、方法加上Scheduled注解的，必须加上@Async让任务异步处理
 */
@Configuration
@EnableConfigurationProperties(TaskThreadPoolProperties.class)
@EnableScheduling
@Slf4j
public class ScheduleAutoConfiguration implements SchedulingConfigurer {

    @Resource
    private TaskThreadPoolProperties threadPoolProperties;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        log.info("<<<<<<<<<<<<<<< Schedule任务调度的线程池ThreadPool >>>>>>>>>>>>>>>>>>");

        taskRegistrar.setScheduler(taskExecutor());
//        taskRegistrar.setCronTasks();
//        taskRegistrar.setFixedDelayTasks();
//        taskRegistrar.setTriggerTasksList();
    }

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskExecutor() {
//        return Executors.newScheduledThreadPool(100);

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(threadPoolProperties.getCorePoolSize()); // 设置线程池容量
        scheduler.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());  //设置线程名开头
        scheduler.setAwaitTerminationSeconds(threadPoolProperties.getKeepAliveSeconds()); // 当调度器shutdown被调用时等待当前被调度的任务完成
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setRemoveOnCancelPolicy(true); // 设置当任务被取消的同时从当前调度器移除的策略
        return scheduler;

    }
}
