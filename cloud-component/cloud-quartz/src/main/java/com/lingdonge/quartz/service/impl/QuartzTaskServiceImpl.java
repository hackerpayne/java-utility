package com.lingdonge.quartz.service.impl;

import cn.hutool.core.exceptions.UtilException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lingdonge.quartz.enums.JobStatusEnum;
import com.lingdonge.quartz.job.BaseJob;
import com.lingdonge.quartz.job.QuartzJobFactoryRun;
import com.lingdonge.quartz.mapper.ScheduleJobMapper;
import com.lingdonge.quartz.model.ScheduleJob;
import com.lingdonge.quartz.service.QuartzTaskService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
@Slf4j
public class QuartzTaskServiceImpl implements QuartzTaskService {

    //加入Qulifier注解，通过名称注入bean
    @Autowired
    @Qualifier("scheduler")
    private Scheduler scheduler;

    @Resource
    private ScheduleJobMapper scheduleJobMapper;

    /**
     * 加载并运行定时任务里面的配置
     */
    @Override
    public void loadAndRunTask() {

        log.info("遍历并加载数据库任务列表并执行");

        //查询数据库是否存在需要定时的任务
        List<ScheduleJob> scheduleJobs = scheduleJobMapper.selectList(new QueryWrapper<ScheduleJob>().eq(ScheduleJob.JOB_STATUS, JobStatusEnum.ENABLE.getValue()));
        if (scheduleJobs != null) {
            scheduleJobs.forEach(this::runJob);
        }
    }

    /**
     * 添加任务，如果任务已经在运行则更新，如果任务没有，则添加任务
     *
     * @param scheduleJob
     */
    @Override
    public void runJob(ScheduleJob scheduleJob) {

        //添加触发调度名称
        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());

        CronTrigger cronTrigger;

        try {
            cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            if (null == cronTrigger) {

                log.info("任务[" + scheduleJob.getJobName() + "]不存在，准备重新添加并运行");

                //设置触发时间
                CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());

                //触发建立
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(scheduleJob.getJobName(), scheduleJob.getJobGroup())
                        .withSchedule(cronScheduleBuilder).build();

                //添加作业名称
                JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());

                //建立作业
                JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactoryRun.class).withIdentity(jobKey).build();

                //传入调度的数据，在QuartzFactory中需要使用
                jobDetail.getJobDataMap().put("scheduleJob", scheduleJob);

                //调度作业
                scheduler.scheduleJob(jobDetail, trigger);
            } else {

                log.info("任务[" + scheduleJob.getJobName() + "]已经在调度中，准备更新Cron表达式继续运行");


                /* Trigger已存在，那么更新相应的定时设置 */
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());

                /* 按新的cronExpression表达式重新构建trigger */
                cronTrigger = cronTrigger.getTriggerBuilder()
                        .withIdentity(triggerKey).withSchedule(scheduleBuilder)
                        .build();

                /* 按新的trigger重新设置job执行 */
                scheduler.rescheduleJob(triggerKey, cronTrigger);
            }

        } catch (Exception ex) {
            log.error("Task init failed.", ex);
        }
//        添加Job监听器
//        QuartzJobListener quartzJobListener = new QuartzJobListener("quartzListener", jobManagerService);
//        scheduler.getListenerManager().addJobListener(quartzJobListener, allJobs());
    }


    /**
     * JobDetail工厂对象生成JobDetail
     *
     * @param jobClass    job类
     * @param description 任务描述
     * @param groupName   分组名
     * @return JobDetail
     */
    @Override
    public JobDetail addJob(Class<? extends Job> jobClass, String description, String groupName) {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(jobClass);
        jobDetailFactoryBean.setDurability(true);
        jobDetailFactoryBean.setName(jobClass.getName());
        jobDetailFactoryBean.setGroup(groupName);
        jobDetailFactoryBean.setDescription(description);
        jobDetailFactoryBean.afterPropertiesSet();
        return jobDetailFactoryBean.getObject();
    }

    /**
     * 添加任务
     *
     * @param job
     * @throws Exception
     */
    @Override
    public void addJob(ScheduleJob job) throws Exception {
        if (checkExists(job.getJobName(), job.getJobGroup())) {
            log.info("===> AddJob fail, job already exist, jobGroup:{}, jobName:{}", job.getJobGroup(), job.getJobName());
            throw new UtilException(String.format("Job已经存在, jobName:{%s},jobGroup:{%s}", job.getJobName(), job.getJobGroup()));
        }

        //添加作业名称
        JobKey jobKey = JobKey.jobKey(job.getJobName(), job.getJobGroup());

        //构建job信息
        JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactoryRun.class).withIdentity(jobKey).build();
        jobDetail.getJobDataMap().put("scheduleJob", job); //调度数据传进去

        //表达式调度构建器(即任务执行的时间)
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());

        //按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobName(), job.getJobGroup()).withSchedule(scheduleBuilder).build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            log.error("创建定时任务失败" + e);
            throw new Exception("创建定时任务失败");
        }
    }

    /**
     * 暂停一个job
     *
     * @param scheduleJob
     * @throws SchedulerException
     */
    @Override
    public void pauseJob(ScheduleJob scheduleJob) {
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            log.error("Task pause failed.", e);
        }
    }


    /**
     * 恢复一个job
     *
     * @param scheduleJob
     * @throws SchedulerException
     */
    @Override
    public void resumeJob(ScheduleJob scheduleJob) {
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            log.error("Task resume failed.", e);
        }
    }

    /**
     * 恢复继续执行Job任务
     *
     * @param jobClassName
     * @param jobGroupName
     * @throws Exception
     */
    @Override
    public void resumeJob(String jobClassName, String jobGroupName) throws Exception {
        scheduler.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
    }


    /**
     * 删除一个job
     *
     * @param scheduleJob
     * @throws SchedulerException
     */
    @Override
    public void deleteJob(ScheduleJob scheduleJob) {
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.error("Task delete failed.", e);
        }
    }

    /**
     * 删除任务
     *
     * @param jobName
     * @param jobGroup
     * @throws Exception
     */
    @Override
    public void deleteJob(String jobName, String jobGroup) throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        try {
            if (checkExists(jobName, jobGroup)) {
                scheduler.pauseTrigger(triggerKey);
                scheduler.unscheduleJob(triggerKey);
                scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));

                log.info("===> delete, triggerKey:{}", triggerKey);
            }
        } catch (SchedulerException e) {
            throw new SchedulerException(e);
        }
    }

    /**
     * 立即执行job
     *
     * @param scheduleJob
     * @throws SchedulerException
     */
    @Override
    public void runJobNow(ScheduleJob scheduleJob) {
        JobKey jobKey = JobKey.jobKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());
        try {
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            log.error("Task run failed.", e);
        }
    }


    /**
     * 更新job时间表达式
     *
     * @param scheduleJob
     * @throws SchedulerException
     */
    @Override
    public void updateJob(ScheduleJob scheduleJob) throws SchedulerException {

        if (!checkExists(scheduleJob.getJobName(), scheduleJob.getJobGroup())) {
            throw new UtilException(String.format("Job不存在, jobName:{%s},jobGroup:{%s}", scheduleJob.getJobName(), scheduleJob.getJobGroup()));
        }

        TriggerKey triggerKey = TriggerKey.triggerKey(scheduleJob.getJobName(), scheduleJob.getJobGroup());

        // 获取trigger，即在spring配置文件中定义的 bean id="schedulerFactoryBean"
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression());

        // 按新的cronExpression表达式重新构建trigger
        trigger = trigger.getTriggerBuilder()
                .withIdentity(triggerKey)
                .withSchedule(scheduleBuilder).build();

        // 按新的trigger重新设置job执行
        scheduler.rescheduleJob(triggerKey, trigger);
    }


    /**
     * 重新定义Job
     *
     * @param jobClassName
     * @param jobGroupName
     * @param cronExpression
     * @throws Exception
     */
    @Override
    public void jobReschedule(String jobClassName, String jobGroupName, String cronExpression) throws Exception {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);

            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);

        } catch (SchedulerException e) {
            log.error("更新定时任务失败" + e);
            throw new Exception("更新定时任务失败");
        }
    }


    /**
     * @param classname
     * @return
     * @throws Exception
     */
    public static BaseJob getClass(String classname) throws Exception {
        Class<?> class1 = Class.forName(classname);
        return (BaseJob) class1.newInstance();
    }

    /**
     * 判断表达式是否可用
     *
     * @param cron
     * @return
     * @throws
     */
    public boolean checkCron(String cron) {
        try {
            CronScheduleBuilder.cronSchedule(cron);
        } catch (Exception e) {
            return (false);
        }
        return (true);
    }

    /**
     * 检查任务是否存在
     *
     * @param jobName  任务名称
     * @param jobGroup 任务分组
     * @return
     * @throws SchedulerException
     */
    public boolean checkExists(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        return scheduler.checkExists(triggerKey);
    }

}