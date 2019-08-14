package com.lingdonge.quartz.service;

import com.lingdonge.quartz.model.ScheduleJob;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

public interface QuartzTaskService {

    /**
     *
     */
    void loadAndRunTask();

    /**
     * @param scheduleJob
     */
    void runJob(ScheduleJob scheduleJob);

    /**
     * JobDetail工厂对象生成JobDetail
     *
     * @param jobClass    job类
     * @param description 任务描述
     * @param groupName   分组名
     * @return JobDetail
     */
    JobDetail addJob(Class<? extends Job> jobClass, String description, String groupName);

    /**
     * 添加任务
     *
     * @param job
     * @throws Exception
     */
    void addJob(ScheduleJob job) throws Exception;

    /**
     * 暂停一个job
     *
     * @param scheduleJob
     * @throws SchedulerException
     */
    void pauseJob(ScheduleJob scheduleJob);

    /**
     * 恢复一个job
     *
     * @param scheduleJob
     * @throws SchedulerException
     */
    void resumeJob(ScheduleJob scheduleJob);

    /**
     * 删除一个job
     *
     * @param scheduleJob
     * @throws SchedulerException
     */
    void deleteJob(ScheduleJob scheduleJob);

    /**
     * 立即执行job
     *
     * @param scheduleJob
     * @throws SchedulerException
     */
    void runJobNow(ScheduleJob scheduleJob);

    /**
     * 更新job时间表达式
     *
     * @param scheduleJob
     * @throws SchedulerException
     */
    void updateJob(ScheduleJob scheduleJob) throws SchedulerException;

    /**
     * 重新定义Job
     *
     * @param jobClassName
     * @param jobGroupName
     * @param cronExpression
     * @throws Exception
     */
    void jobReschedule(String jobClassName, String jobGroupName, String cronExpression) throws Exception;

    /**
     * 恢复继续执行Job任务
     *
     * @param jobClassName
     * @param jobGroupName
     * @throws Exception
     */
    void resumeJob(String jobClassName, String jobGroupName) throws Exception;

    /**
     * 删除任务
     *
     * @param jobClassName
     * @param jobGroupName
     * @throws Exception
     */
    void deleteJob(String jobClassName, String jobGroupName) throws Exception;

}
