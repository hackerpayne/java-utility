package com.lingdonge.core.threads;

/**
 * 多线程实现2：实现Runnable接口，实现其run()方法，创建其实例，并通过该实例构造 Thread ，通过Thread来启动。
 * 调用方法：
 * //线程启动
 * Runnable runnable = new testThreads2();
 * new Thread(runnable).start();
 * //查看Thread类的run方法可以发现 其实现如下：
 * public void run(){
 *  if(target!=null){
 *      target.run();
 *  }
 * Created by Kyle on 16/10/30.
 */
public class testThreads2 implements Runnable {

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
    }


    void Test()
    {


    }
}

