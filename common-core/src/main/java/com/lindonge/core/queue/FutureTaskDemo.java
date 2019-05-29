package com.lindonge.core.queue;

import java.util.concurrent.TimeUnit;

/**
 * https://my.oschina.net/u/2307589/blog/1833322
 */
public class FutureTaskDemo {

    public static void main(String[] args) throws InterruptedException {
        // 子线程
        Thread t = new Thread(() -> {
            CacheBean<String> cb = new CacheBean<>(k -> {
                try {
                    System.out.println("模拟计算数据，计算时长2秒。key=" + k);
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "你好：" + k;
            }, 5000);

            try {
                while (true) {
                    System.out.println("thead2:" + cb.compute("b"));
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();

        // 主线程
        while (true) {
            CacheBean<String> cb = new CacheBean<>(k -> {
                try {
                    System.out.println("模拟计算数据，计算时长2秒。key=" + k);
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "你好：" + k;
            }, 5000);

            System.out.println("thead1:" + cb.compute("b"));
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
