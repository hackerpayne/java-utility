package com.lingdonge.redis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kyle on 17/4/12.
 */

public class ClientThread extends Thread {

    int i = 0;

    public ClientThread(int i) {
        this.i = i;
    }

    @Override
    public void run() {

        System.out.println("开始运行线程：" + i);

        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date);

        // 方案一：内部加锁
//        Object lock = new Object();
//        synchronized (lock) {
//            Jedis jedis = JedisUtils.getJedis();
//            jedis.set("foo", time + Integer.toString(i));
//
//            try {
//                Thread.sleep(1 * 1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            String foo = JedisUtils.getString("foo");
//            System.out.println("【输出>>>>】foo:" + foo + " 第：" + i + "个线程" + "当前时间：" + format.format(new Date()));
//
//        }


    }
}
