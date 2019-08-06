package com.lingdonge.rabbit.consumerThread1;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * 孙线程是最底层的处理线程，其最终要释放孙信号量及对countDownLatch减一
 * 指标计算及存储
 *
 * @author kyle
 */
@Slf4j
public class WorkJobThread implements Runnable {

    private Semaphore innerSemaphore;
    private CountDownLatch countDownLatch;
    private Map<String, Object> threadDelivery;

    public WorkJobThread(CountDownLatch countDownLatch, Semaphore innerSemaphore, Map<String, Object> threadDelivery) {
        this.countDownLatch = countDownLatch;
        this.innerSemaphore = innerSemaphore;
        this.threadDelivery = threadDelivery;
    }


    @Override
    public void run() {
        try {
            String idxName = threadDelivery.get("idx_name").toString();

            String params = threadDelivery.get("params").toString();
            threadDelivery.remove("params");

            threadDelivery.put("start_time", System.currentTimeMillis());

            //调用indexCal 层指标计算api，计算指标
            //考虑idxName为idxCal_01
            Object idx;
            if (idxName.contains("_")) {
                String idxCalName = idxName.substring(0, idxName.lastIndexOf("_"));
//                idx = IdxCalHandler.handle(idxCalName, params);
            } else {
//                idx = IdxCalHandler.handle(idxName, params);
            }

            threadDelivery.put("end_time", System.currentTimeMillis());
//            threadDelivery.put("idx_result", idx);
            log.info(idxName + "cal is finished!", new Object[0]);

            //save to mongo
//            saveToDB(threadDelivery);
            log.info(idxName + "cal result had saved into mongo", new Object[0]);

        } catch (Exception e) {
            log.error("单个指标计算过程发生错误");
        } finally { //最终要释放孙信号量及countdown减一
            innerSemaphore.release();
            countDownLatch.countDown();
        }
    }
}