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
 * 自定义定时任务
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
        scheduler.setPoolSize(threadPoolProperties.getCorePoolSize());
        scheduler.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());  //设置线程名开头
        scheduler.setAwaitTerminationSeconds(threadPoolProperties.getKeepAliveSeconds());
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;

    }
}
