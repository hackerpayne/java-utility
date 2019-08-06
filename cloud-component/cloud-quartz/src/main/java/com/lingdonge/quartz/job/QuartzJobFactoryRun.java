package com.lingdonge.quartz.job;

import com.lingdonge.quartz.domain.ScheduleJob;
import com.lingdonge.spring.SpringContextUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 可以执行Job里面的任务，统一从数据库中读出来之统一进行处理和调度
 */
@DisallowConcurrentExecution
public class QuartzJobFactoryRun implements Job {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap().get("scheduleJob");
        logger.info("任务名称 = [" + scheduleJob.getJobName() + "]");

        // 获取对应的Bean
        Object object = SpringContextUtil.getBean(scheduleJob.getSpringId());
        try {
            //利用反射执行对应方法
            Method method = object.getClass().getMethod(scheduleJob.getMethodName());
            method.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}