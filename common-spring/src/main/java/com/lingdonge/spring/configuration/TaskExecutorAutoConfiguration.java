package com.lingdonge.spring.configuration;

import com.lingdonge.spring.configuration.properties.TaskThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义异步线程池 http://cxytiandi.com/blog/detail/12919
 * 添加配置： spring.task.pool.maxPoolSize=100
 * 使用时：@Async("taskExecutor")
 * <p>
 * TaskExecutor来实现多线程和并发编程
 * ThreadPoolTaskExecutor可实现一个基于线程池的TaskExecutor。
 */
//@Configuration
@EnableConfigurationProperties(TaskThreadPoolProperties.class) // 开启指定类的配置
@EnableAsync //利用@EnableAsync注解开启异步任务支持
//@ConditionalOnProperty(name = "spring.task.pool")// 必须开启配置才会启动多线程配置类
@Slf4j
public class TaskExecutorAutoConfiguration implements AsyncConfigurer { //配置类实现AsyncConfigurer接口并重写getAsyncExcutor方法，并返回一个ThreadPoolTaskExevutor,就获得了一个基于线程池的TaskExecutor

    @Resource
    private TaskThreadPoolProperties taskThreadPoolProperties;

    /**
     * 定义Spring的线程池处理类
     * 使用Spring托管，可以在不需要的时候优雅关闭线程池
     *
     * @return
     */
    @Bean
    @Primary //设置为首选线程池
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskThreadPoolProperties.getCorePoolSize());//核心线程数10：线程池创建时候初始化的线程数
        executor.setMaxPoolSize(taskThreadPoolProperties.getMaxPoolSize());//最大线程数20：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setQueueCapacity(taskThreadPoolProperties.getQueueCapacity());//缓冲队列200：用来缓冲执行任务的队列
        executor.setKeepAliveSeconds(taskThreadPoolProperties.getKeepAliveSeconds());//允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setThreadNamePrefix(taskThreadPoolProperties.getThreadNamePrefix());

        //线程池对拒绝任务（无线程可用）的处理策略，目前只支持AbortPolicy、CallerRunsPolicy
        //AbortPolicy:直接抛出java.token.concurrent.RejectedExecutionException异常 -->
        //CallerRunsPolicy:主线程直接执行该任务，执行完之后尝试添加下一个任务到线程池中，可以有效降低向线程池内添加任务的速度 -->
        //DiscardOldestPolicy:抛弃旧的任务、暂不支持；会导致被丢弃的任务无法再次被执行 -->
        //DiscardPolicy:抛弃当前任务、暂不支持；会导致被丢弃的任务无法再次被执行 -->
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.setWaitForTasksToCompleteOnShutdown(true);// 设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        executor.setAwaitTerminationSeconds(60); //设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住。

        executor.initialize();//当为bean的时候不需要调用此方法，装载容器的时候回自动调用
        return executor;

//        return new LazyTraceExecutor(beanFactory, executor); // 使用 Sleuth 时，需要通过懒加载，确保traceId和spanId正确的传递

    }

    /**
     * 异步线程池配置
     * 在使用 @Async("myTaskAsyncPool") 时，可以指定自己的线程池配置
     * 在执行方法上添加@Async注解来声明這个方法是异步方法。如果注解在类上，则表明该类的所有方法都是异步的，而這里的方法自动被注入使用ThreadPoolTaskExecutor作为TaskExecutor。
     *
     * @return
     */
    @Override
    public Executor getAsyncExecutor() {
        log.info("<<<<<<<<<<<<<<< 加载 AsyncExecutor 服务 >>>>>>>>>>>>>>>>>>");

        return threadPoolTaskExecutor();
    }

    /**
     * 异步任务中异常处理
     *
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable arg0, Method arg1, Object... arg2) {
                log.error("==========================" + arg0.getMessage() + "=======================", arg0);
                log.error("exception method:" + arg1.getName());
            }
        };
    }
}