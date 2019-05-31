package com.lingdonge.core.threads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

/**
 * 多线程并发任务,取结果归集
 */
public class CompletableFutureDemo {

    public void simpleTest() {
        StringBuilder result = new StringBuilder();
        List<String> messages = Arrays.asList("a", "b", "c");
        List<CompletableFuture> futures = messages.stream()
                .map(msg -> CompletableFuture.completedFuture(msg).thenApplyAsync(s -> s))
                .collect(Collectors.toList());

        CompletableFuture allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .whenComplete((v, th) -> {
                    futures.forEach(cf -> System.out.println(cf));
                    result.append("done");
                });
        allOf.join();
    }

    public static void main(String[] args) {
        Long start = System.currentTimeMillis();
        //结果集
        List<String> list = new ArrayList<String>();
        List<String> list2 = new ArrayList<String>();
        //定长10线程池
        ExecutorService exs = Executors.newFixedThreadPool(10);
        List<CompletableFuture<String>> futureList = new ArrayList<>();
        final List<Integer> taskList = Lists.newArrayList(2, 1, 3, 4, 5, 6, 7, 8, 9, 10);
        try {
            ////方式一：循环创建CompletableFuture list,调用sequence()组装返回一个有返回值的CompletableFuture，返回结果get()获取
            //for(int i=0;i<taskList.size();i++){
            //    final int j=i;
            //    //异步执行
            //    CompletableFuture<String> future = CompletableFuture.supplyAsync(()->calc(taskList.get(j)), exs)
            //        //Integer转换字符串    thenAccept只接受不返回不影响结果
            //        .thenApply(e->Integer.toString(e))
            //        //如需获取任务完成先后顺序，此处代码即可
            //        .whenComplete((v, e) -> {
            //            System.out.println("任务"+v+"完成!result="+v+"，异常 e="+e+","+new Date());
            //            list2.add(v);
            //        })
            //        ;
            //    futureList.add(future);
            //}
            ////流式获取结果：此处是根据任务添加顺序获取的结果
            //list = sequence(futureList).get();

            //方式二：全流式处理转换成CompletableFuture[]+组装成一个无返回值CompletableFuture，join等待执行完毕。返回结果whenComplete获取
            CompletableFuture[] cfs = taskList.stream()
                    .map(object -> CompletableFuture.supplyAsync(() -> calc(object), exs)
                            .thenApply(h -> Integer.toString(h))
                            //如需获取任务完成先后顺序，此处代码即可
                            .whenComplete((v, e) -> {
                                System.out.println("任务" + v + "完成!result=" + v + "，异常 e=" + e + "," + new Date());
                                list2.add(v);
                            })).toArray(CompletableFuture[]::new);
            //等待总任务完成，但是封装后无返回值，必须自己whenComplete()获取
            CompletableFuture.allOf(cfs).join();
            System.out.println("任务完成先后顺序，结果list2=" + list2 + "；任务提交顺序，结果list=" + list + ",耗时=" + (System.currentTimeMillis() - start));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exs.shutdown();
        }
    }

    public static Integer calc(Integer i) {
        try {
            if (i == 1) {
                //任务1耗时3秒
                Thread.sleep(3000);
            } else if (i == 5) {
                //任务5耗时5秒
                Thread.sleep(5000);
            } else {
                //其它任务耗时1秒
                Thread.sleep(1000);
            }
            System.out.println("task线程：" + Thread.currentThread().getName() + "任务i=" + i + ",完成！+" + new Date());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return i;
    }


}