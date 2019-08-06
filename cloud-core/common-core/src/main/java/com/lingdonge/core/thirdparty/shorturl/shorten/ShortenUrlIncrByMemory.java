package com.lingdonge.core.thirdparty.shorturl.shorten;

import com.lingdonge.core.thirdparty.shorturl.util.ShortUrlUtil;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 短址算法一：自增序列算法 也叫永不重复算法
 * 设置 id 自增，一个 10进制 id 对应一个 62进制的数值，1对1，也就不会出现重复的情况。这个利用的就是低进制转化为高进制时，字符数会减少的特性。
 * 短址的长度一般设为 6 位，而每一位是由 [a - z, A - Z, 0 - 9] 总共 62 个字母组成的，所以 6 位的话，总共会有 62^6 ~= 568亿种组合，基本上够用了。
 * 这里附上一个进制转换工具 http://tool.lu/hexconvert/ 上图的数据就是用这个工具生成的。
 * <p>
 * 优点：就是简单好理解，永不重复。但是短码的长度不固定，随着 id 变大从一位长度开始递增。如果非要让短码长度固定也可以就是让 id 从指定的数字开始递增就可以了。百度短网址用的这种算法。上文说的开源短网址项目 YOURLS 也是采用了这种算法。
 */
public class ShortenUrlIncrByMemory implements ShortenUrlInterface {

    private AtomicLong sequence = new AtomicLong(0);

    /**
     * 使用内存递增生成短址
     *
     * @param longUrl
     * @return
     */
    @Override
    public String shorten(String longUrl) {
        return shorten(longUrl, 1);
    }

    /**
     * 使用内存递增生成短网址
     *
     * @param longUrl
     * @param shortLength
     * @return
     */
    @Override
    public String shorten(String longUrl, Integer shortLength) {
        long myseq = sequence.incrementAndGet();
        String shortUrl = ShortUrlUtil.shortenBy62Radix(myseq);
        return shortUrl;
    }


}
