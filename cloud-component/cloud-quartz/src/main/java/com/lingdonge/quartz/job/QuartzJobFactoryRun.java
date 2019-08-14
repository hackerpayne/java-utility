package com.lingdonge.quartz.job;

import com.lingdonge.quartz.model.ScheduleJob;
import com.lingdonge.spring.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.lang.reflect.Method;

/**
 * 可以执行Job里面的任务，统一从数据库中读出来之统一进行处理和调度
 */
@DisallowConcurrentExecution
@Slf4j
public class QuartzJobFactoryRun implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get("scheduleJob");
        log.info("任务名称 = [" + scheduleJob.getJobName() + "]");

        // 获取对应的Bean
        Object object = SpringContextUtil.getBean(scheduleJob.getSpringId());
        try {
            //利用反射执行对应方法
            Method method = object.getClass().getMethod(scheduleJob.getMethodName());
            method.invoke(object);
        } catch (Exception e) {
            log.error("QuartzJobFactoryRun发生异常：{}", e.getMessage());
        }

    }

}