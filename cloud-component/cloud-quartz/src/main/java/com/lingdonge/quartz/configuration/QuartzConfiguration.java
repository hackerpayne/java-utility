package com.lingdonge.quartz.configuration;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableScheduling
@Slf4j
public class QuartzConfiguration {

    @Resource
    private QuartzJobFactory quartzJobFactory;

    /**
     * 解决Spring注入的问题
     *
     * @param applicationContext
     * @return
     */
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /**
     * 导入配置文件中的配置信息
     *
     * @return
     * @throws IOException
     */
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        //在quartz.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    /**
     * 定义quartz调度工厂
     *
     * @return
     */
    @Bean(name = "schedulerFactory")
    public SchedulerFactoryBean schedulerFactory() throws IOException {

        log.info("<<<<<<<<<<<<<<< 重写 Quartz SchedulerFactoryBean 处理 >>>>>>>>>>>>>>>>>>");

        SchedulerFactoryBean bean = new SchedulerFactoryBean();

        bean.setJobFactory(quartzJobFactory);// 单独调用Spring注入
//        bean.setJobFactory(jobFactory);//直接调用

        // 用于quartz集群,QuartzScheduler 启动时更新己存在的Job
        bean.setOverwriteExistingJobs(true);

        // 延时启动，应用启动1秒后
//        bean.setStartupDelay(5);

        // 注册触发器
//        bean.setTriggers(jobTrigger);

        // 设置配置文件中的配置进来
        bean.setQuartzProperties(quartzProperties());

        return bean;
    }

    /**
     * quartz初始化监听器
     *
     * @return
     */
    @Bean
    public QuartzInitializerListener executorListener() {
        return new QuartzInitializerListener();
    }

    /**
     * 通过SchedulerFactoryBean获取Scheduler的实例
     *
     * @return
     * @throws IOException
     * @throws SchedulerException
     */
    @Bean(name = "scheduler")
    public Scheduler scheduler() throws IOException, SchedulerException {
        Scheduler scheduler = schedulerFactory().getScheduler();

        log.info("<<<<<<<<<<<<<<< 重写 Quartz Scheduler 启动中 >>>>>>>>>>>>>>>>>>");

        scheduler.start();
        return scheduler;
    }


    /**
     * 解决Spring注入的问题
     */
    class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

        private transient AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(final ApplicationContext context) {
            beanFactory = context.getAutowireCapableBeanFactory();
        }

        @Override
        protected Object createJobInstance(final TriggerFiredBundle bundle)
                throws Exception {
            final Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            return job;
        }
    }
}
