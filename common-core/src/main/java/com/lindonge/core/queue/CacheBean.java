package com.lindonge.core.queue;

import java.util.concurrent.*;

/**
 * 延时内存队列，用于在内存里面延时获取数据
 * 代码来源：https://my.oschina.net/u/2307589/blog/1833322
 * <p>
 * DelayQueue是一个支持延时获取元素的无界阻塞队列。DelayQueue内部队列使用PriorityQueue来实现。队列中的元素必须实现Delayed接口，在创建元素时可以指定多久才能从队列中获取当前元素。只有在延迟期满时才能从队列中提取元素。
 * 应用：
 * 1、缓存系统的设计：可以用DelayQueue保存缓存元素的有效期，使用一个线程循环查询DelayQueue，一旦能从DelayQueue中获取元素时，表示缓存有效期到了。
 * 2、定时任务调度：使用DelayQueue保存当天将会执行的任务和执行时间，一旦DelayQueue中获取到任务就开始执行，比如TimerQueue就是使用DelayQueue实现的。
 */
public class CacheBean<V> {
    // 缓存计算的结果
    private final static ConcurrentMap<String, Future<Object>> cache = new ConcurrentHashMap<>();

    // 延迟队列来判断那些缓存过期
    private final static DelayQueue<DelayedItem<String>> delayQueue = new DelayQueue<>();

    // 缓存时间
    private final int ms;

    static {
        // 定时清理过期缓存
        Thread t = new Thread() {
            @Override
            public void run() {
                dameonCheckOverdueKey();
            }
        };
        t.setDaemon(true);
        t.start();
    }

    private final Computable<V> c;

    /**
     * @param c Computable
     */
    public CacheBean(Computable<V> c) {
        this(c, 60 * 1000);
    }

    /**
     * @param c  Computable
     * @param ms 缓存多少毫秒
     */
    public CacheBean(Computable<V> c, int ms) {
        this.c = c;
        this.ms = ms;
    }

    public V compute(final String key) throws InterruptedException {

        while (true) {
            //根据key从缓存中获取值
            Future<V> f = (Future<V>) cache.get(key);
            if (f == null) {
                Callable<V> eval = new Callable<V>() {
                    public V call() {
                        return (V) c.compute(key);
                    }
                };
                FutureTask<V> ft = new FutureTask<>(eval);
                //如果缓存中存在此可以，则返回已存在的value
                f = (Future<V>) cache.putIfAbsent(key, (Future<Object>) ft);
                if (f == null) {
                    //向delayQueue中添加key，并设置该key的存活时间
                    delayQueue.put(new DelayedItem<>(key, ms));
                    f = ft;
                    ft.run();
                }
            }
            try {
                return f.get();
            } catch (CancellationException e) {
                cache.remove(key, f);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查过期的key，从cache中删除
     */
    private static void dameonCheckOverdueKey() {
        DelayedItem<String> delayedItem;
        while (true) {
            try {
                delayedItem = delayQueue.take();
                if (delayedItem != null) {
                    cache.remove(delayedItem.getT());
                    System.out.println(System.nanoTime() + " remove " + delayedItem.getT() + " from cache");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}