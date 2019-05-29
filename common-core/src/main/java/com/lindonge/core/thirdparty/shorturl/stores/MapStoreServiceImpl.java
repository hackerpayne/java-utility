package com.lindonge.core.thirdparty.shorturl.stores;

import com.lindonge.core.thirdparty.shorturl.StoreService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MapStoreServiceImpl implements StoreService {

    private Map<String, String> longShortUrlMap = new ConcurrentHashMap<String, String>();

    private Map<String, String> shortLongUrlMap = new ConcurrentHashMap<String, String>();

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

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

    @Override
    public String getDataByShortUrl(String shortUrl) {
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

    @Override
    public String getDataByLongUrl(String longUrl) {
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
