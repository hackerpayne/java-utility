package com.lingdonge.core.threads;

import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class CircularCounterTest {

    public static void test() {
        CircularCounter c = new CircularCounter();
        AtomicInteger count = new AtomicInteger(0);
        List<Thread> li = new ArrayList<Thread>();
        int size = 10;
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            Thread t = new Thread(new CounterRunner(c, latch1, latch2, count), "thread-" + i);
            li.add(t);
            t.start();
        }
        System.out.println("start");
        latch1.countDown();
        try {
            latch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(count.get());
        System.out.println(c.get());
        if (count.get() == c.get()) {
            System.out.println("该计数器是线程安全的！！！");
        }
    }

    @Test
    public void testWithThreads() {

        System.out.println(LocalDate.now());

        for (int i = 0; i < 15; i++) {
            test();
        }
    }

}
