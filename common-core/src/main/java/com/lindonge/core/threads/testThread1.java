package com.lindonge.core.threads;

/**
 * 多线程实现方式一：
 * 启动：new testThread1().start();
 * 继承Thread类型，覆写run()方法，run方法里面实现自己需要的逻辑，new Thread()之后 线程进入创建状态，start()之后进入就绪状态，进入运行状态时机取决于系统CPU调度。
 * Created by Kyle on 16/10/30.
 */
public class testThread1 extends Thread {

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
    }
}
