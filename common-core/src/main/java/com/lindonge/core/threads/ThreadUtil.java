package com.lindonge.core.threads;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 线程池工具
 */
@Slf4j
public class ThreadUtil {

    /**
     * #
     */
    private final static String POUND = "#";
    /**
     * -
     */
    private final static String MINUS_SIGN = "-";

    private static ExecutorService pool = Executors.newFixedThreadPool(50);

    public static void submitTask(Runnable runnable) {
        pool.submit(runnable);
    }

    /**
     * 新建一个线程池
     *
     * @param threadSize 同时执行的线程数大小
     * @return ExecutorService
     */
    public static ExecutorService newExecutor(int threadSize) {
        return Executors.newFixedThreadPool(threadSize);
    }

    /**
     * 获得一个新的线程池
     *
     * @return ExecutorService
     */
    public static ExecutorService newExecutor() {
        return Executors.newCachedThreadPool();
    }

    /**
     * 获得一个新的线程池，只有单个线程
     *
     * @return ExecutorService
     */
    public static ExecutorService newSingleExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    /**
     * 创建线程池
     *
     * @param corePoolSize
     * @param maximumPoolSize
     * @return
     */
    public static ThreadPoolExecutor newExecutor(int corePoolSize, int maximumPoolSize) {
        return newExecutor("custom", corePoolSize, maximumPoolSize);
    }

    /**
     * 获得一个新的线程池<br>
     * 如果maximumPoolSize =》 corePoolSize，在没有新任务加入的情况下，多出的线程将最多保留60s
     *
     * @param prefix          线程池名称前缀
     * @param corePoolSize    初始线程池大小
     * @param maximumPoolSize 最大线程池大小
     * @return {@link ThreadPoolExecutor}
     */
    public static ThreadPoolExecutor newExecutor(String prefix, int corePoolSize, int maximumPoolSize) {

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat(prefix + "-thread-%d").build();

        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), namedThreadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 获得一个新的线程池<br>
     * 传入阻塞系数，线程池的大小计算公式为：CPU可用核心数 / (1 - 阻塞因子)<br>
     * Blocking Coefficient(阻塞系数) = 阻塞时间／（阻塞时间+使用CPU的时间）<br>
     * 计算密集型任务的阻塞系数为0，而IO密集型任务的阻塞系数则接近于1。
     * <p>
     * see: http://blog.csdn.net/partner4java/article/details/9417663
     *
     * @param blockingCoefficient 阻塞系数，阻塞因子介于0~1之间的数，阻塞因子越大，线程池中的线程数越多。
     * @return {@link ThreadPoolExecutor}
     * @since 3.0.6
     */
    public static ThreadPoolExecutor newExecutorByBlockingCoefficient(float blockingCoefficient) {
        if (blockingCoefficient > 1 || blockingCoefficient <= 0) {
            throw new IllegalArgumentException("[blockingCoefficient] must between 0 and 1, or equals 0.");
        }

        // 最佳的线程数 = CPU可用核心数 / (1 - 阻塞系数)
        int poolSize = (int) (Runtime.getRuntime().availableProcessors() / (1 - blockingCoefficient));

        return new ThreadPoolExecutor(poolSize, poolSize, //
                0L, TimeUnit.MILLISECONDS, //
                new LinkedBlockingQueue<Runnable>());
    }

    /**
     * 直接在公共线程池中执行线程
     *
     * @param runnable 可运行对象
     */
    public static void execute(Runnable runnable) {
        GlobalThreadPool.execute(runnable);
    }

    /**
     * 执行异步方法
     *
     * @param runnable 需要执行的方法体
     * @param isDeamon 是否守护线程。守护线程会在主线程结束后自动结束
     * @return 执行的方法体
     */
    public static Runnable excAsync(final Runnable runnable, boolean isDeamon) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                runnable.run();
            }
        };
        thread.setDaemon(isDeamon);
        thread.start();

        return runnable;
    }

    /**
     * 执行有返回值的异步方法<br>
     * Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞
     *
     * @param <T>  回调对象类型
     * @param task {@link Callable}
     * @return Future
     */
    public static <T> Future<T> execAsync(Callable<T> task) {
        return GlobalThreadPool.submit(task);
    }

    /**
     * 执行有返回值的异步方法<br>
     * Future代表一个异步执行的操作，通过get()方法可以获得操作的结果，如果异步操作还没有完成，则，get()会使当前线程阻塞
     *
     * @param runnable 可运行对象
     * @return {@link Future}
     * @since 3.0.5
     */
    public static Future<?> execAsync(Runnable runnable) {
        return GlobalThreadPool.submit(runnable);
    }

    /**
     * 新建一个CompletionService，调用其submit方法可以异步执行多个任务，最后调用take方法按照完成的顺序获得其结果。<br>
     * 若未完成，则会阻塞
     *
     * @param <T> 回调对象类型
     * @return CompletionService
     */
    public static <T> CompletionService<T> newCompletionService() {
        return new ExecutorCompletionService<T>(GlobalThreadPool.getExecutor());
    }

    /**
     * 新建一个CompletionService，调用其submit方法可以异步执行多个任务，最后调用take方法按照完成的顺序获得其结果。<br>
     * 若未完成，则会阻塞
     *
     * @param <T>      回调对象类型
     * @param executor 执行器 {@link ExecutorService}
     * @return CompletionService
     */
    public static <T> CompletionService<T> newCompletionService(ExecutorService executor) {
        return new ExecutorCompletionService<T>(executor);
    }

    /**
     * 新建一个CountDownLatch，一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待。
     *
     * @param threadCount 线程数量
     * @return CountDownLatch
     */
    public static CountDownLatch newCountDownLatch(int threadCount) {
        return new CountDownLatch(threadCount);
    }


    /**
     * 重复开启 threadNum 个线程来执行 runnable
     *
     * @param runnable  可执行任务
     * @param threadNum 重复开启的线程个数
     * @param sleepTime 启动完所有线程后，休息 ms
     */
    public static void startThread(Runnable runnable, String threadName, int threadNum, long sleepTime) {
        for (int i = 0; i < threadNum; i++) {
            Thread thread = new Thread(runnable, POUND + StringUtils.defaultIfEmpty(threadName, "Thread") + MINUS_SIGN + i);
            thread.start();
        }
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 重复开启 threadNum 个线程来执行 runnable
     *
     * @param runnable  可执行任务
     * @param threadNum 重复开启的线程个数
     * @param sleepTime 启动完所有线程后，休息 ms
     */
    public static void startThread(Runnable runnable, int threadNum, long sleepTime) {
        ThreadUtil.startThread(runnable, "Thread", threadNum, sleepTime);
    }

    /**
     * 开启 1 个线程来执行 runnable
     *
     * @param runnable 可执行任务
     */
    public static void startThread(Runnable runnable) {
        startThread(runnable, 1, 0);
    }

    /**
     * 开启 1 个线程来执行 runnable
     *
     * @param runnable 可执行任务
     */
    public static void startThread(Runnable runnable, String threadName) {
        startThread(runnable, StringUtils.trimToEmpty(threadName), 1, 0);
    }

    /**
     * 重复开启 threadNum 个线程来执行 runnable
     *
     * @param runnable  可执行任务
     * @param sleepTime 重复开启的线程个数
     */
    public static void startThread(Runnable runnable, long sleepTime) {
        startThread(runnable, 1, sleepTime);
    }

    /**
     * 挂起当前线程
     *
     * @param timeout  挂起的时长
     * @param timeUnit 时长单位
     * @return 被中断返回false，否则true
     */
    public static boolean sleep(Number timeout, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(timeout.longValue());
        } catch (InterruptedException e) {
            handleInterruptedException();
            return false;
        }
        return true;
    }

    /**
     * 挂起当前线程
     *
     * @param millis 挂起的毫秒数
     * @return 被中断返回false，否则true
     */
    public static boolean sleep(Number millis) {
        if (millis == null) {
            return true;
        }

        try {
            Thread.sleep(millis.longValue());
        } catch (InterruptedException e) {
            handleInterruptedException();
            return false;
        }
        return true;
    }


    /**
     * 纯粹为了提醒下处理InterruptedException的正确方式，除非你是在写不可中断的任务.
     */
    public static void handleInterruptedException() {
        Thread.currentThread().interrupt();
    }

    /**
     * 考虑{@link Thread#sleep(long)}方法有可能时间不足给定毫秒数，此方法保证sleep时间不小于给定的毫秒数
     *
     * @param millis 给定的sleep时间
     * @return 被中断返回false，否则true
     * @see ThreadUtil#sleep(Number)
     */
    public static boolean safeSleep(Number millis) {
        long millisLong = millis.longValue();
        long done = 0;
        while (done < millisLong) {
            long before = System.currentTimeMillis();
            if (!sleep(millisLong - done)) {
                return false;
            }
            long after = System.currentTimeMillis();
            done += (after - before);
        }
        return true;
    }

    /**
     * @return 获得堆栈列表
     */
    public static StackTraceElement[] getStackTrace() {
        return Thread.currentThread().getStackTrace();
    }

    /**
     * 获得堆栈项
     *
     * @param i 第几个堆栈项
     * @return 堆栈项
     */
    public static StackTraceElement getStackTraceElement(int i) {
        StackTraceElement[] stackTrace = getStackTrace();
        if (i < 0) {
            i += stackTrace.length;
        }
        return stackTrace[i];
    }

    /**
     * 创建本地线程对象
     *
     * @param <T>           持有对象类型
     * @param isInheritable 是否为子线程提供从父线程那里继承的值
     * @return 本地线程
     */
    public static <T> ThreadLocal<T> createThreadLocal(boolean isInheritable) {
        if (isInheritable) {
            return new InheritableThreadLocal<>();
        } else {
            return new ThreadLocal<>();
        }
    }

    /**
     * 结束线程，调用此方法后，线程将抛出 {@link InterruptedException}异常
     *
     * @param thread 线程
     * @param isJoin 是否等待结束
     */
    public static void interupt(Thread thread, boolean isJoin) {
        if (null != thread && !thread.isInterrupted()) {
            thread.interrupt();
            if (isJoin) {
                waitForDie(thread);
            }
        }
    }

    /**
     * 等待线程结束. 调用 {@link Thread#join()} 并忽略 {@link InterruptedException}
     *
     * @param thread 线程
     */
    public static void waitForDie(Thread thread) {
        boolean dead = false;
        do {
            try {
                thread.join();
                dead = true;
            } catch (InterruptedException e) {
                //ignore
            }
        } while (!dead);
    }

    /**
     * 获取JVM中与当前线程同组的所有线程<br>
     *
     * @return 线程对象数组
     */
    public static Thread[] getThreads() {
        return getThreads(Thread.currentThread().getThreadGroup().getParent());
    }

    /**
     * 获取JVM中与当前线程同组的所有线程<br>
     * 使用数组二次拷贝方式，防止在线程列表获取过程中线程终止<br>
     * from Voovan
     *
     * @param group 线程组
     * @return 线程对象数组
     */
    public static Thread[] getThreads(ThreadGroup group) {
        final Thread[] slackList = new Thread[group.activeCount() * 2];
        final int actualSize = group.enumerate(slackList);
        final Thread[] result = new Thread[actualSize];
        System.arraycopy(slackList, 0, result, 0, actualSize);
        return result;
    }

    /**
     * 获取进程的主线程<br>
     * from Voovan
     *
     * @return 进程的主线程
     */
    public static Thread getMainThread() {
        for (Thread thread : getThreads()) {
            if (thread.getId() == 1) {
                return thread;
            }
        }
        return null;
    }

    /**
     * 组合多个CompletableFuture为一个CompletableFuture, 所有子任务全部完成，组合后的任务才会完成。带返回值，可直接get.
     *
     * @param futures
     * @param <T>
     * @return
     */
    public static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        //1.构造一个空CompletableFuture，子任务数为入参任务list size
        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        //2.流式（总任务完成后，每个子任务join取结果，后转换为list）
        return allDoneFuture.thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

    /**
     * Stream流式类型futures转换成一个CompletableFuture, 所有子任务全部完成，组合后的任务才会完成。带返回值，可直接get.
     *
     * @param futures
     * @param <T>
     * @return
     */
    public static <T> CompletableFuture<List<T>> sequence(Stream<CompletableFuture<T>> futures) {
        List<CompletableFuture<T>> futureList = futures.filter(f -> f != null).collect(Collectors.toList());
        return sequence(futureList);
    }

}
