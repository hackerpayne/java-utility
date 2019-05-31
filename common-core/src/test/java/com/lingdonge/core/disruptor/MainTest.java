package com.lingdonge.core.disruptor;

public class MainTest {

    public static void main(String[] args) throws InterruptedException {

        DisruptorManager.init(new DataEventHandler());
        for (long l = 0; true; l++) {
            DisruptorManager.putDataToQueue(l);
            Thread.sleep(1000);
        }
    }
}
