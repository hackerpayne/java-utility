package com.lingdonge.core.threads;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class AtomCounter {
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public void increment() {
        atomicInteger.incrementAndGet();
    }

    public void decrement() {
        atomicInteger.decrementAndGet();
    }

    public Integer value() {
        return atomicInteger.get();
    }

}
