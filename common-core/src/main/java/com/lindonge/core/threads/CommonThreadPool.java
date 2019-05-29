package com.lindonge.core.threads;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.*;

/**
 * 自定义某个线程池，在退出程序时自动销毁
 * 也可以使用Bean的方式创建
 */
@Slf4j
public class CommonThreadPool {


    /**
     * 线程池
     */
    private ExecutorService executor;

    /**
     * 初始化10个线程
     */
    @PostConstruct
    void init() {

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("demo-pool-%d").build();

        // 通用线程池
        executor = new ThreadPoolExecutor(5, 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());

    }

    /**
     * 提交新的消费者
     *
     * @param shutdownableThread
     */
    public void SubmitConsumerPool(Runnable shutdownableThread) {
        executor.execute(shutdownableThread);
    }

    /**
     * 程序关闭,关闭线程池
     */
    @PreDestroy
    void fin() {
        shutdown();
    }

    public void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }

        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                log.info("CommonThreadPool退出超时，exiting uncleanly");
            }
        } catch (InterruptedException e) {
            log.info("CommonThreadPool退出中断, exiting uncleanly");
        }
    }


}
