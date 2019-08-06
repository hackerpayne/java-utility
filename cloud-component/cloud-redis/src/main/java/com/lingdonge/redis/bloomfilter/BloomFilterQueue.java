package com.lingdonge.redis.bloomfilter;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 布隆过滤器，自己完善的类
 */
@Slf4j
public class BloomFilterQueue {

//    private Logger log = LoggerFactory.getLogger(getClass());

    private int expectedInsertions;

    private double fpp;

    private AtomicInteger counter;

    public BloomFilterQueue(int expectedInsertions) {
        this(expectedInsertions, 0.01);
    }

    /**
     * @param expectedInsertions the number of expected insertions to the constructed
     * @param fpp                the desired false positive probability (must be positive and less than 1.0)
     */
    public BloomFilterQueue(int expectedInsertions, double fpp) {
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        this.bloomFilter = rebuildBloomFilter();
    }

    protected BloomFilter<CharSequence> rebuildBloomFilter() {
        counter = new AtomicInteger(0);
        return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, fpp);
    }

    private final BloomFilter<CharSequence> bloomFilter;

    /**
     * 小五新增，可以把已经采集的列表，预热到队列内。实现已采集的数据的内存去重
     * 可以和RedisScheduler并用
     *
     * @param item
     */
    public void addItem(String item) {
        boolean isDuplicate = bloomFilter.mightContain(item);
        if (!isDuplicate) {
            bloomFilter.put(item);
            counter.incrementAndGet();
        }
    }

    /**
     * 判断队列里面是否已经有这条数据。true有的话，将会跳过，不加入队列，false没有，将会加入采集队列
     *
     * @return
     */
    public boolean contains(String item) {
        return bloomFilter.mightContain(item);
    }

    public void reset() {
        rebuildBloomFilter();
    }

    public int getTotalCount() {
        return counter.get();
    }
}
