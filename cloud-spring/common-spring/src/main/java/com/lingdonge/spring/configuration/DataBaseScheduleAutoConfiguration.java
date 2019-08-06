package com.lingdonge.spring.configuration;

import com.lingdonge.spring.SpringContextUtil;
import com.lingdonge.spring.cron.CronService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

/**
 * 通过数据库配置定时器的定时任务
 */
//@Configuration
@EnableScheduling
@Slf4j
public class DataBaseScheduleAutoConfiguration implements SchedulingConfigurer {

    @Autowired
    @SuppressWarnings("all")
    private CronService cronScervice;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {

        // 设置线程池
        scheduledTaskRegistrar.setScheduler(taskScheduler());

        // 定时任务，根据Bean来获取
        Runnable runnable = (Runnable) SpringContextUtil.getBean(cronScervice.getBeanName());

        scheduledTaskRegistrar.addTriggerTask(runnable,

                //2.设置执行周期(Trigger)
                triggerContext -> {

                    //2.1 从数据库获取执行周期
                    String cron = cronScervice.getCron();

                    //2.2 合法性校验.
                    if (StringUtils.isEmpty(cron)) {
                        // Omitted Code ..
                        log.error("Cron表达式为空，");
                    }

                    //2.3 返回执行周期(Date)
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                });

    }


    /**
     * 并行任务使用策略：多线程处理（配置线程数等）
     *
     * @return ThreadPoolTaskScheduler 线程池
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskScheduler()
    {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(20);
        scheduler.setThreadNamePrefix("task-");  //设置线程名开头
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }
}
