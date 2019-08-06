package com.lingdonge.core.thirdparty.shorturl.stores;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 使用内存Map做为存储器，读取和返回短址信息
 */
public class ShortUrlStoreByMap implements ShortUrlStoreInterface {

    private Map<String, String> longShortUrlMap = new ConcurrentHashMap<String, String>();

    private Map<String, String> shortLongUrlMap = new ConcurrentHashMap<String, String>();

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * 保存短址对应的长网址到Map里面
     *
     * @param longUrl
     * @param shortUrl
     * @return
     */
    @Override
    public Boolean saveShortUrl(String longUrl, String shortUrl) {
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();
            longShortUrlMap.put(longUrl, shortUrl);
            shortLongUrlMap.put(shortUrl, longUrl);
        } catch (Exception e) {
        } finally {
            writeLock.unlock();
        }
        return true;
    }

    /**
     * 根据短址查长网址
     *
     * @param shortUrl
     * @return
     */
    @Override
    public String getByShortUrl(String shortUrl) {
        String longUrl = null;
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            longUrl = shortLongUrlMap.get(shortUrl);
        } catch (Exception e) {
        } finally {
            readLock.unlock();
        }
        return longUrl;
    }

    /**
     * 根据长网址反查短网址
     *
     * @param longUrl
     * @return
     */
    @Override
    public String getByLongUrl(String longUrl) {
        String shortUrl = null;
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            shortUrl = longShortUrlMap.get(longUrl);
        } catch (Exception e) {
        } finally {
            readLock.unlock();
        }
        return shortUrl;
    }
}
