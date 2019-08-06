package com.lingdonge.core.threads;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SplitListWithExecutor {

    /**
     * 多线程处理10万条数据
     * @return
     */
    public String test1() {
        try {
            //10万条数据
            List<String> list = new ArrayList<>();
            List<String> list2 = new ArrayList<>();

            for (int i = 1; i <= 100000; i++) {
                list.add("multithreading:" + i);
            }

            //每条线程处理的数据尺寸
            int size = 250;
            int count = list.size() / size;
            if (count * size != list.size()) {
                count++;
            }
            int countNum = 0;
            final CountDownLatch countDownLatch = new CountDownLatch(count);
            ExecutorService executorService = Executors.newFixedThreadPool(8);
            ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);

            while (countNum < list.size()) {
                countNum += size;

                ConCallable callable = new ConCallable();
                //截取list的数据，分给不同线程处理
                callable.setList(ImmutableList.copyOf(list.subList(countNum - size, countNum < list.size() ? countNum : list.size())));
                ListenableFuture listenableFuture = listeningExecutorService.submit(callable);

                Futures.addCallback(listenableFuture, new FutureCallback<List<String>>() {
                    @Override
                    public void onSuccess(List<String> list1) {
                        countDownLatch.countDown();
                        list2.addAll(list1);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        countDownLatch.countDown();
                        log.info("处理出错：", throwable);

                    }
                });
            }
            countDownLatch.await(30, TimeUnit.MINUTES);
            log.info("符合条件的返回数据个数为：" + list2.size());
            log.info("回调函数：" + list2.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "正在处理......";

    }


}
