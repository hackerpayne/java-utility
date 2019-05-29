package com.lingdonge.rabbit.consumerThread1;

import com.alibaba.fastjson.JSONObject;
import com.kyle.utility.encrypt.Md5Util;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

/**
 * 一条消息分解成多个指标线程并行计算
 * MessageSplitToMany子线程进一步将消息拆分并交给一定数量的孙线程处理（用semaphore限制并发数），在这些孙线程未全部处理完成前，MessageSplitToMany子线程阻塞。这里CountDownLatch正好满足需求。
 *
 * @author kyle
 */
@Slf4j
public class MessageSplitToMany implements Runnable {

    private Map<String, String> threadDelivery;
    private Semaphore semaphore;
    private Executor executor;
    private int allowedChildThreads;

    public MessageSplitToMany(Map<String, String> threadDelivery, Semaphore semaphore, Executor executor, int allowedChildThreads) {
        this.threadDelivery = threadDelivery;
        this.semaphore = semaphore;
        this.executor = executor;
        this.allowedChildThreads = allowedChildThreads;
    }

    @Override
    public void run() {
        try {
            String params = threadDelivery.get("params");
            log.info(params, new Object[0]);
            JSONObject multiIdxParams = JSONObject.parseObject(params);
            //对并发处理单个指标计算的工作线程数量有一定的约束,所允许的工作线程数量不能超过设定值
            if (allowedChildThreads > multiIdxParams.size()) {
                allowedChildThreads = multiIdxParams.size();
            }

            Semaphore innerSemaphore = new Semaphore(allowedChildThreads);
            CountDownLatch countDownLatch = new CountDownLatch(multiIdxParams.size());
            for (String idxName : multiIdxParams.keySet()) {
                String idxParams = multiIdxParams.getString(idxName);

                Map<String, Object> childThreadDelivery = new LinkedHashMap<>();
                childThreadDelivery.put("job_id", threadDelivery.get("job_id"));
                childThreadDelivery.put("index_id", Md5Util.getMd5(idxName + idxParams));
                childThreadDelivery.put("job_type", threadDelivery.get("job_type"));
                childThreadDelivery.put("idx_name", idxName);
                childThreadDelivery.put("params", idxParams);

                innerSemaphore.acquire(); //孙信号量控制子线程中的并发数
                executor.execute(new WorkJobThread(countDownLatch, innerSemaphore, childThreadDelivery));//将countDownLatch及孙信号量传给IdxCalAndSaveThread孙线程，由它countdown及释放孙信号量
            }

            countDownLatch.await(); //在message中所有指标计算完成前主线程阻塞，直至countdown减为0

            log.info("-----" + Thread.currentThread().getName() + "consumer thread finished the all idxCals Cal-----");

        } catch (Exception e) {
            log.error("并发处理消息过程发生错误");
        } finally {
            semaphore.release(); //释放子信号量，这个信号量是从主线程传递进来的
        }
    }

}