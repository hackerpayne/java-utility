package com.lingdonge.encache.core;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * EnCache缓存辅助类
 */
public class EnCacheUtil {

    public static CacheManager manager = CacheManager.create();

    public static String USER_CACHE = "userCache";
    public static String SYS_CACHE = "sysCache";
    public static String CODE_CACHE = "codeCache";

    public static Object get(String cacheName, Object key) {
        Cache cache = manager.getCache(cacheName);
        if (cache != null) {
            Element element = cache.get(key);
            if (element != null) {
                return element.getObjectValue();
            }
        }
        return null;
    }

    public static void put(String cacheName, Object key, Object value) {
        Cache cache = manager.getCache(cacheName);
        if (cache != null) {
            cache.put(new Element(key, value));
        }
    }

    public static void putPhone(String phone, Object value) {
        removePhone(phone);
        EnCacheUtil.put(EnCacheUtil.CODE_CACHE, phone, value);
        Cache cache = manager.getCache(EnCacheUtil.CODE_CACHE);
        if (cache != null) {
            cache.put(new Element(phone, value));
        }
    }

    public static boolean removePhone(String phone) {
        Cache cache = manager.getCache(EnCacheUtil.CODE_CACHE);
        if (cache != null) {
            return cache.remove(phone);
        }
        return false;
    }

    public static boolean remove(String cacheName, Object key) {
        Cache cache = manager.getCache(cacheName);
        if (cache != null) {
            return cache.remove(key);
        }
        return false;
    }

    public static void main(String[] args) {
        String key = "key";
        String value = "hello";
        EnCacheUtil.put(USER_CACHE, key, value);
        System.out.println(EnCacheUtil.get(USER_CACHE, key));
    }


}
