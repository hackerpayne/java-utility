package com.lingdonge.core.dates;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 程序计时类，可以计算开始到结束所花时间，非线程安全
 */
@Slf4j
public class TimeCounter {

    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

    final private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws InterruptedException {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                TimeCounter.markStart();

                try {
                    Thread.sleep(RandomUtils.nextInt(2, 5) * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TimeCounter.markEnd();
            }
        };


        Thread th1 = new Thread(runnable);
        th1.setName("th1");
        th1.start();

        Thread.sleep(3 * 1000);

        Thread th2 = new Thread(runnable);
        th2.setName("th2");
        th2.start();

        Thread.sleep(4 * 1000);
        Thread th3 = new Thread(runnable);
        th3.setName("th3");
        th3.start();


    }

    /**
     * 开始时间
     */
    public static void markStart() {
        System.out.println(MessageFormat.format("当前线程[{0}]，任务开始，当前时间：{1}", Thread.currentThread().getName(), format.format(new Date())));
        log.info(MessageFormat.format("当前线程[{0}]，任务开始，当前时间：{1}", Thread.currentThread().getName(), format.format(new Date())));
        startTime.set(System.currentTimeMillis());

    }

    /**
     * 结束时间
     */
    public static void markEnd() {
        long endTime = System.currentTimeMillis();
        System.out.println(MessageFormat.format("当前线程[{0}]，任务结束，当前时间：{1}", Thread.currentThread().getName(), format.format(new Date())));
        log.info(MessageFormat.format("当前线程[{0}]，任务结束，当前时间：{1}", Thread.currentThread().getName(), format.format(new Date())));


        long costMillis = endTime - startTime.get();

        System.out.println(MessageFormat.format("当前线程[{0}]，总共耗时：{1}", Thread.currentThread().getName(), DateUtil.formatTime(costMillis)));
        log.info(MessageFormat.format("当前线程[{0}]，总共耗时：{1}", Thread.currentThread().getName(), DateUtil.formatTime(costMillis)));

    }

}
