package com.lingdonge.core.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 内存计数器，多线程进行内存的数据计算
 */
public class MemoryCounter {

    private final ConcurrentHashMap<String, AtomicInteger> counts = new ConcurrentHashMap<String, AtomicInteger>();

    /**
     * 指定用户自增1
     *
     * @param user
     */
    public void increment(String user) {
        while (true) {
            AtomicInteger current = counts.get(user);
            if (current == null) {
                // 没有数据的初始化
                counts.putIfAbsent(user, new AtomicInteger());
                continue;
            }

            int value = current.incrementAndGet();
            if (value > 0) {
                //we have incremented the counter
                break;
            } else {
                //someone is flushing this key, remove it
                //so we can increment on our next iteration
                counts.replace(user, current, new AtomicInteger());
            }

        }
    }

    /**
     * @param user
     * @return
     */
    public Integer get(String user) {
        return counts.getOrDefault(user, new AtomicInteger(0)).intValue();
    }


}
