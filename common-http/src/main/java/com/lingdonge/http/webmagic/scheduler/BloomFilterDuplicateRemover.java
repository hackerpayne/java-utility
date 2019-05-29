package com.lingdonge.http.webmagic.scheduler;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.lingdonge.http.webmagic.Request;
import com.lingdonge.http.webmagic.Task;
import com.lingdonge.http.webmagic.scheduler.component.DuplicateRemover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 大量URL使用布隆过滤器
 * BloomFilterDuplicateRemover for huge number of urls.
 */
public class BloomFilterDuplicateRemover implements DuplicateRemover {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private int expectedInsertions;

    private double fpp;

    private AtomicInteger counter;

    public BloomFilterDuplicateRemover(int expectedInsertions) {
        this(expectedInsertions, 0.01);
    }

    /**
     *
     * @param expectedInsertions the number of expected insertions to the constructed
     * @param fpp the desired false positive probability (must be positive and less than 1.0)
     */
    public BloomFilterDuplicateRemover(int expectedInsertions, double fpp) {
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        this.bloomFilter = rebuildBloomFilter();
    }

    protected BloomFilter<CharSequence> rebuildBloomFilter() {
        counter = new AtomicInteger(0);
        return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, fpp);
    }


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
     * 小五新增，判断过滤器里面有多少数据了
     *
     * @return
     */
    public Integer getTotalCount() {
        return counter.get();
    }

    private final BloomFilter<CharSequence> bloomFilter;

    /**
     * 判断队列里面是否已经有这条数据。true有的话，将会跳过，不加入队列，false没有，将会加入采集队列
     *
     * @param request
     * @param task
     * @return
     */
    @Override
    public boolean isDuplicate(Request request, Task task) {

        // 小五新增，可以强制重新进行抓取
        Object reCrawle = request.getExtra("recrawl");
        if (reCrawle != null && reCrawle.toString().equals("true")) {//没标记，需要去重
            logger.info("URL:{}被标记为可重抓取，此URL将不会被加入重复判断队列", getUrl(request));
            return false;//返回true，表示此URL已经在队列中存在，false队列中没有。也别加进去，防止跳过
        }

//        logger.info("使用BloomFilterNew进行重复判断");

        boolean isDuplicate = bloomFilter.mightContain(getUrl(request));
        if (!isDuplicate) {
            bloomFilter.put(getUrl(request));
            counter.incrementAndGet();
        }
        return isDuplicate;
    }

    protected String getUrl(Request request) {
        return request.getUrl();
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        rebuildBloomFilter();
    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return counter.get();
    }
}