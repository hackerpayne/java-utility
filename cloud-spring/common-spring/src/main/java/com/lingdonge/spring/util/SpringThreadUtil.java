package com.lingdonge.spring.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Spring的线程池管理工具类
 */
@Slf4j
public class SpringThreadUtil {

    /**
     * 创建ThreadExecutor线程池
     *
     * @param prefix
     * @param corePoolSize
     * @param maxPoolSize
     * @param queueSize
     * @return
     */
    public static ThreadPoolTaskExecutor createThreadExecutor(String prefix, Integer corePoolSize, Integer maxPoolSize, Integer queueSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);//线程池活跃的线程数
        //executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(maxPoolSize);//线程池最大活跃的线程数
        executor.setThreadNamePrefix(prefix);//线程名称前缀

        // 线程池维护线程所允许的空闲时间
        executor.setKeepAliveSeconds(30 * 60);

        executor.setAllowCoreThreadTimeOut(true); // 核心线程池也会请0

        //线程池拒绝机制
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        //executor.setWaitForTasksToCompleteOnShutdown(true);

        executor.setQueueCapacity(queueSize);  // 线程池队列

        executor.initialize();

        return executor;
    }


}
