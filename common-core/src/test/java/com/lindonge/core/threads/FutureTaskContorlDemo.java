package com.lindonge.core.threads;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * FutureTask弥补了Future必须用线程池提交返回Future的缺陷，实现功能如下：
 * 1、Runnable接口，可开启线程执行。
 * 2、Future<v>接口，可接受Callable接口的返回值，futureTask.get()阻塞获取结果。
 * 这两个步骤：一个开启线程执行任务，一个阻塞等待执行结果，分离这两步骤，可在这两步中间穿插别的相关业务逻辑。
 */
public class FutureTaskContorlDemo {

    public static void main(String[] args) {
        try {
            System.out.println("=====例如一个统计公司总部和分部的总利润是否达标100万==========");
            //利润
            Integer count = 0;
            //1.定义一个futureTask，假设去远程http获取各个分公司业绩.
            FutureTask<Integer> futureTask = new FutureTask<Integer>(new CallableTask());
            Thread futureTaskThread = new Thread(futureTask);
            futureTaskThread.start();
            System.out.println("futureTaskThread start！" + new Date());

            //2.主线程先做点别的事
            System.out.println("主线程查询总部公司利润开始时间：" + new Date());
            Thread.sleep(5000);
            count += 10;//北京集团总部利润。
            System.out.println("主线程查询总部公司利润结果时间：" + new Date());

            //总部已达标100万利润，就不再继续执行获取分公司业绩任务了
            if (count >= 100) {
                System.out.println("总部公司利润达标，取消futureTask！" + new Date());
                futureTask.cancel(true);//不需要再去获取结果，那么直接取消即可
            } else {
                System.out.println("总部公司利润未达标，进入阻塞查询分公司利润！" + new Date());
                //3总部未达标.阻塞获取，各个分公司结果
                Integer i = futureTask.get();//真正执行CallableTask
                System.out.println("i=" + i + "获取到结果!" + new Date() + new Date());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author diandian.zhang
     * @ClassName:CallableTask
     * @Description:一个十分耗时的任务
     * @date 2017年6月16日上午10:39:04
     */
    static class CallableTask implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("CallableTask-call，查询分公司利润，执行开始！" + new Date());
            Thread.sleep(10000);
            System.out.println("CallableTask-call，查询分公司利润，执行完毕！" + new Date());
            return 10;
        }
    }
}