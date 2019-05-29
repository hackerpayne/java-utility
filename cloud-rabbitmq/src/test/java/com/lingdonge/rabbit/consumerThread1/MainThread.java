package com.lingdonge.rabbit.consumerThread1;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
public class MainThread {

    public static void main(String[] args) {

    }

    private int threadNum = 10;
    private Semaphore semaphore = new Semaphore(threadNum);
    private Executor executor = Executors.newCachedThreadPool();
    private int allowedChildThreads = 10;

    /**
     * 主线程监听消息队列，并将接受的消息交给其他线程去处理，并且处理消息的其他线程数量有限制。
     * 这里考虑用concurrent包里的semaphore，信号量可以认为是一定数量num的许可证，主线程将消息和一张许可证交给一个子线程去处理，子线程处理完了（不管成功或失败）都要将许可证上交给主线程，供下一个子线程使用，这样就可能出现两种情况：
     * 1）许可证富裕，主线程继续将消息交给子线程去消费；
     * 2）许可证已被多个子线程占用，还未交回主线程手里。这样主线程就阻塞了。
     */
    public void doJob() {
        log.info("begin consuming!", new Object[0]);
//        MessageClientFactory.getClient().consume("xxxQueue", true, new MessageConsumer() {
//            @Override
//            public void consume(String message) {
//                log.info(message, new Object[0]);
//
//                try {
//                    JSONObject messageObject = JSONObject.parseObject(message);
//
//                    String jobType = messageObject.getString("job_type"); //任务类型
//                    String jobID = messageObject.getString("job_id"); //任务ID
//                    Date jobSubmitTime = messageObject.getDate("job_submit_time"); //任务提交时间
//                    String params = messageObject.getString("params"); //任务指标参数
//
//                    //构建线程间信息传递对象
//                    Map<String, String> threadDelivery = new HashMap<>();
//                    threadDelivery.put("job_type", jobType);
//                    threadDelivery.put("job_id", jobID);
//                    threadDelivery.put("params", params);
//
//                    semaphore.acquire(); //许可证减少一张
//                    executor.execute(new MessageSplitToMany(threadDelivery, semaphore, executor, allowedChildThreads)); //将消费信息和许可证一并交给子线程MessageSplitToMany，他处理完释放许可证
//                } catch (Exception e) {
//                    log.error((Marker) e, "消费主线程发生错误", new Object[0]);
//                }
//            }
//        });

    }
}
