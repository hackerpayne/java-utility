//package com.kyle.tests;
//
//import redis.RedisQueue;
//import com.kyle.threads.ThreadUtil;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.token.*;
//
///**
// * 处理数据
// * Created by kyle on 17/4/14.
// */
//public class DBManager {
//
//    public static Logger logger = LoggerFactory.getLogger(DBManager.class);
//
////    public static Set<String> ipSet = Sets.newConcurrentHashSet();
////    public static Set<String> uaSet = Sets.newConcurrentHashSet();
//
//
//    public static RedisQueue redisIPQueue = new RedisQueue("queue_ip");
//    public static RedisQueue redisUserAgentQueue = new RedisQueue("queue_ua");
//
//    public static void addIPAddress(String ipAddress) {
////        ipSet.add(ipAddress);
//        redisIPQueue.add(ipAddress);
//
//    }
//
//
//    public static void addUserAgent(String userAgent) {
//        redisUserAgentQueue.add(userAgent);
//        //uaSet.add(ipAddress);
//    }
//
//    /**
//     * 监控当前的数据量
//     */
//    public static void monitorThread() {
//
//        Thread monitor = new Thread() {
//
//            public void run() {
//
//                while (true) {
//                    ThreadUtil.sleep(5000);
//
//                    logger.info("当前Quene共有IP【" + redisIPQueue.count() + "】条，UA【" + redisUserAgentQueue.count() + "】条");
//
//                }
//
//            }
//        };
//
//        monitor.start();
//
//    }
//
//
//    /**
//     * 开启线程单独进行数据处理
//     */
//    public static void startInsertDBThread() throws InterruptedException {
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                logger.info("开始线程任务");
//
//                while (true) {
//
//                    if (!ParseNginxLogToDB.flagStatus) {
//
//                        logger.info("文件尚未处理完毕，请等待。。。");
//
//                        ThreadUtil.sleep(2000);
//                        continue;
//                    }
//
//                    //队列不为空，开始处理里面的数据
//                    if (redisIPQueue.count() < 1) {
//
//                        logger.info("队列处理完毕，任务 结束");
////                        break;
//
//                    } else {
//                        String ipAddr = redisIPQueue.pop();
//
////                        logger.info("队列IP数据：" + ipAddr);
//
//                        DBSaverTemplate.insertUA(ipAddr, "");
//                    }
//
//                    //队列不为空，开始处理里面的数据
//                    if (redisUserAgentQueue.count() < 1) {
//
//                        logger.info("队列处理完毕，任务 结束");
////                        break;
//
//                    } else {
//                        String userAgent = redisUserAgentQueue.pop();
//
////                        logger.info("队列UA数据：" + userAgent);
//
//                        DBSaverTemplate.insertUA("", userAgent);
//                    }
//
//
//                }
//
//            }
//        };
//
//        Thread th = new Thread(runnable);
//        th.setName("插入线程");
//        th.start();
//
////        th.join();
//    }
//
//    /**
//     * 另开线程，5秒一插入数据库
//     */
//    public static void startThreads() {
//
//        //循环取出数据插入数据库
//        List<String> listData = new ArrayList();
//        String item;
//        while (true) {
//
////            //取出50条，进行处理
////            Iterator<String> iterator = ipSet.iterator();
////            while (iterator.hasNext()) {
////                for (int i = 0; i < 50; i++) {
////                    item = iterator.next();
////                    listData.add(item);
////                    ipSet.remove(item);
////                }
////            }
//
//        }
//    }
//
//
//}
