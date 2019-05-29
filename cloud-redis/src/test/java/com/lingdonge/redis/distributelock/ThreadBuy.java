package com.lingdonge.redis.distributelock;


class ThreadBuy extends Thread {

    private ServiceOrder service;

    public ThreadBuy(ServiceOrder service) {
        this.service = service;
    }

    @Override
    public void run() {
        service.handleOder();
    }

}