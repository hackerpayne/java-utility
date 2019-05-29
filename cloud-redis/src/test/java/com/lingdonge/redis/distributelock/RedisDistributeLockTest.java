package com.lingdonge.redis.distributelock;


class RedisDistributeLockTest {

    /**
     * 模拟20个线程下订单
     */
    void tryLock() {

        ServiceOrder service = new ServiceOrder();
        for (int i = 0; i < 20; i++) {
            ThreadBuy client = new ThreadBuy(service);
            client.start();
        }
    }

    void tryLock1() {
    }

    void unlock() {
    }
}