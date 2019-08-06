package com.lingdonge.redis.distributelock;


import com.lingdonge.redis.distributelock.RedisDistributeLock;

import java.util.UUID;

class ServiceOrder {

    private final int MAX = 10;

    RedisDistributeLock DLock = new RedisDistributeLock();

    int n = 10;

    public void handleOder() {

        String userName = UUID.randomUUID().toString().substring(0, 8) + Thread.currentThread().getName();

        DLock.tryLock("Huawei Mate 10：" + userName, 10000);
        System.out.println("正在为用户：" + userName + " 处理订单");

        if (n > 0) {
            int num = MAX - n + 1;
            System.out.println("用户：" + userName + "购买第" + num + "台，剩余" + (--n) + "台");
        } else {
            System.out.println("用户：" + userName + "无法购买，已售罄！");
        }

        DLock.unlock("Huawei Mate 10：" + userName);
    }

}