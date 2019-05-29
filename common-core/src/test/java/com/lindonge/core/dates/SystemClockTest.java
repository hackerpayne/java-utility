package com.lindonge.core.dates;

import org.junit.Test;

import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SystemClockTest {


    private Lock lock = new ReentrantLock();




    @Test
    public void testClock() throws InterruptedException {

        Vector<Thread> listThreads = new Vector<>();


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    lock.lock();
                    System.out.println("==================");
                    System.out.println("SysClock:" + SystemClock.now());
                    System.out.println("SysMills:" + System.currentTimeMillis());
                } finally {
                    lock.unlock();
                }
            }
        };

        for (int i = 0; i < 10; i++) {
            Thread th = new Thread(runnable);
            listThreads.add(th);
            th.start();
        }

        for (Thread th : listThreads) {
            th.join();
        }

        System.out.println("所有线程处理完毕");

    }

}
