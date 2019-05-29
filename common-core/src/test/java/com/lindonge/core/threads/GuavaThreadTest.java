package com.lindonge.core.threads;

import com.google.common.util.concurrent.*;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试使用Guava的线程池类
 */
public class GuavaThreadTest {

    /**
     * 默认最大线程数
     */
    public static final int DEFAULT_MAX_THREAD = 1000;

    /**
     * 可监听的线程池
     */
    private static ListeningExecutorService defaultCompletedExecutorService = null;

    /**
     * 默认锁
     */
    private static final Object lock = new Object();

    /**
     * 构造线程池
     *
     * @param maxThreadNumber
     * @param namePrefix
     * @return
     */
    public static ListeningExecutorService newCachedExecutorService(int maxThreadNumber, final String namePrefix) {
        return MoreExecutors.listeningDecorator(new ThreadPoolExecutor(0, maxThreadNumber, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), new ThreadFactory() {

            private final AtomicInteger poolNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, namePrefix + poolNumber.getAndIncrement());
                return thread;
            }
        }));

    }

    public static ListeningExecutorService newCachedExecutorService(String namePrefix) {
        return newCachedExecutorService(DEFAULT_MAX_THREAD, namePrefix);
    }

    public static ListeningExecutorService getDefaultCompletedExecutorService() {
        if (defaultCompletedExecutorService == null) {
            synchronized (lock) {
                if (defaultCompletedExecutorService == null) {
                    defaultCompletedExecutorService = newCachedExecutorService("Completed-Callback-");
                }
            }
        }
        return defaultCompletedExecutorService;
    }

    public static void main(String[] args) {
        Long t1 = System.currentTimeMillis();

        // 任务1
        ListenableFuture<Boolean> booleanTask = GuavaThreadTest.getDefaultCompletedExecutorService()
                .submit(new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        return true;
                    }

                });
        Futures.addCallback(booleanTask, new FutureCallback<Boolean>() {

            @Override
            public void onSuccess(Boolean result) {
                System.out.println("BooleanTask : " + result);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("BooleanTask 执行失败 【" + t.getMessage() + "】 ");
            }
        });

        // 任务2
        ListenableFuture<String> stringTask = GuavaThreadTest.getDefaultCompletedExecutorService()
                .submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return "Hello World";
                    }
                });

        Futures.addCallback(stringTask, new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.err.println("StringTask: " + result);
            }

            @Override
            public void onFailure(Throwable t) {
                System.err.println("StringTask 执行失败 【" + t.getMessage() + "】 ");
            }
        });

        // 任务3
        ListenableFuture<Integer> integerTask = GuavaThreadTest.getDefaultCompletedExecutorService()
                .submit(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return new Random().nextInt(100);
                    }
                });

        Futures.addCallback(integerTask, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println("IntegerTask: " + result);
            }

            @Override
            public void onFailure(Throwable t) {
                System.err.println("IntegerTask 执行失败 【" + t.getMessage() + "】 ");
            }
        });

        Long t2 = System.currentTimeMillis();

        // 执行时间
        System.err.println("time: " + (t2 - t1));
    }

    public static void main1(String[] args) {
        for (int i = 0; i < 10; i++) {
            GuavaThreadTest.getDefaultCompletedExecutorService().submit(new Runnable() {

                @Override
                public void run() {
                    System.out.println("xxxxx");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("xxxxx1");
                }
            });
        }
    }


    public static void guavaFuture() throws Exception {
        System.out.println("-------------------------------- 神秘的分割线 -----------------------------------");
        // 好的实现应该是提供回调,即异步调用完成后,可以直接回调.本例采用guava提供的异步回调接口,方便很多.
        ListeningExecutorService guavaExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
        final ListenableFuture<String> listenableFuture = guavaExecutor.submit(new Callable<String>() {

            @Override
            public String call() throws Exception {
                TimeUnit.SECONDS.sleep(1);
                return "[" + Thread.currentThread().getName() + "]: guava的Future返回结果";
            }
        });

        // 注册监听器,即异步调用完成时会在指定的线程池中执行注册的监听器
        listenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    String logTxt = "[" + Thread.currentThread().getName() + "]: guava对返回结果进行异步CallBack(Runnable):"
                            + listenableFuture.get();
                    System.out.println(logTxt);
                } catch (Exception e) {
                }
            }
        }, Executors.newSingleThreadExecutor());

        // 主线程可以继续执行,异步完成后会执行注册的监听器任务.
        System.out.println("[" + Thread.currentThread().getName() + "]: guavaFuture1执行结束");
    }

    public static void guavaFuture2() throws Exception {
        System.out.println("-------------------------------- 神秘的分割线 -----------------------------------");
        // 除了ListenableFuture,guava还提供了FutureCallback接口,相对来说更加方便一些.
        ListeningExecutorService guavaExecutor2 = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
        final ListenableFuture<String> listenableFuture2 = guavaExecutor2.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                TimeUnit.SECONDS.sleep(1);
                String logText = "[" + Thread.currentThread().getName() + "]: guava的Future返回结果";
                System.out.println(logText);
                return logText;
            }
        });

        // 注意这里没用指定执行回调的线程池,从输出可以看出，<span style="color:#FF0000;">默认是和执行异步操作的线程是同一个.</span>
        Futures.addCallback(listenableFuture2, new FutureCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        String logTxt = "[" + Thread.currentThread().getName() + "]=======>对回调结果【" + result + "】进行FutureCallback,经测试，发现是和回调结果处理线程为同一个线程";
                        System.out.println(logTxt);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                    }
                }
        );
        // 主线程可以继续执行,异步完成后会执行注册的监听器任务.
        System.out.println("[" + Thread.currentThread().getName() + "]: guavaFuture2执行结束");
    }


}
