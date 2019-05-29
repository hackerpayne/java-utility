package com.lingdonge.db.db;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 自动过期的缓存
 * 使用时：CacheManager cm=CacheManager.getInstance();
 * CacheConfig cModel=new CacheConfig();
 * Date d=new Date();
 * cModel.setBeginTime(d.getTime());
 * cModel.setDurableTime(60);
 * cModel.setForever(true);
 * cm.addCache("kk", "123", cModel);//在缓存加值
 * Created by kyle on 17/5/15.
 */
@Slf4j
public class CacheManager {

    private static Map cacheMap = Maps.newConcurrentMap();
    private static Map cacheConfMap = Maps.newConcurrentMap();

    private static CacheManager cm = null;

    public static void main(String[] args) throws InterruptedException {

        CacheManager cache = CacheManager.getInstance();
//        cache.incrValue("multithreading", 2);
//
//        System.out.println(cache.getValue("multithreading"));
//
//        cache.incrValue("multithreading", 1);
//
//        System.out.println(cache.getValue("multithreading"));


//        Long begin = new Date().getTime();
//
//        Thread.sleep(5000);
//
//        Long end = new Date().getTime();
//
//        System.out.println(begin - end);


//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR_OF_DAY, 16);
//        cal.set(Calendar.MINUTE, 29);
//        cal.set(Calendar.SECOND, 0);
//        System.out.println(cal.getTime());
//
//
//        cache.addCache("multithreading", "testValue", cal.getTime());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        while (true) {
//            System.out.println(sdf.format(new Date()) + "，结果为：:" + cache.getValue("multithreading"));
//            Thread.sleep(1000);
//        }

    }

    //构造方法
    private CacheManager() {
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static CacheManager getInstance() {
        if (cm == null) {
            cm = new CacheManager();
            Thread t = new ClearCache();
            t.start();
        }
        return cm;
    }

    /**
     * 设置今天过期
     *
     * @param key
     * @param value
     * @return
     */
    public boolean addCacheExpiresToday(Object key, Object value) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
//        System.out.println(cal.getTime());
        return addCache(key, value, cal.getTime());
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public boolean addCacheForever(Object key, Object value) {
//        log.info("开始增加缓存－－－－－－－－－－－－－");
        boolean flag = false;
        try {
            CacheConfig cModel = new CacheConfig();
            Date d = new Date();
            cModel.setBeginTime(d.getTime());
            cModel.setForever(true);

            cacheMap.put(key, value);
            cacheConfMap.put(key, cModel);
            flag = true;
        } catch (Exception e) {
            log.error("addCache失败", e);
        }

        return flag;
    }

    /**
     * 指定时间过期
     *
     * @param key
     * @param value
     * @param date
     * @return
     */
    public boolean addCache(Object key, Object value, Date date) {
//        log.info("开始增加缓存－－－－－－－－－－－－－");
        boolean flag = false;
        try {
            CacheConfig cModel = new CacheConfig();
            Date d = new Date();

            Long timeDur = (date.getTime() - d.getTime()) / 1000;//指定时间离现在还有多少秒

            cModel.setBeginTime(d.getTime());
            cModel.setDurableTime(timeDur.intValue());
            cModel.setForever(false);

            cacheMap.put(key, value);
            cacheConfMap.put(key, cModel);
//            System.out.println("增加缓存结束－－－－－－－－－－－－－");
            log.info("Now Caches:" + cacheMap.size());
            flag = true;
        } catch (Exception e) {
            log.error("addCache失败", e);
        }

        return flag;
    }

    /**
     * 指定过期时间，单位秒
     *
     * @param key
     * @param value
     * @param expires
     * @return
     */
    public boolean addCache(Object key, Object value, Integer expires) {
//        log.info("开始增加缓存－－－－－－－－－－－－－");
        boolean flag = false;
        try {
            CacheConfig cModel = new CacheConfig();
            Date d = new Date();
            cModel.setBeginTime(d.getTime());
            cModel.setDurableTime(expires);
            cModel.setForever(false);

            cacheMap.put(key, value);
            cacheConfMap.put(key, cModel);
//            System.out.println("增加缓存结束－－－－－－－－－－－－－");
//            System.out.println("now addcache==" + cacheMap.size());
            flag = true;
        } catch (Exception e) {
            log.error("addCache失败", e);
        }

        return flag;
    }

    /**
     * 增加缓存
     *
     * @param key
     * @param value
     * @param ccm   缓存对象
     * @return
     */
    public boolean addCache(Object key, Object value, CacheConfig ccm) {
//        log.info("开始增加缓存－－－－－－－－－－－－－");
        boolean flag = false;
        try {
            cacheMap.put(key, value);
            cacheConfMap.put(key, ccm);
//            System.out.println("增加缓存结束－－－－－－－－－－－－－");
//            System.out.println("now addcache==" + cacheMap.size());
            flag = true;
        } catch (Exception e) {
            log.error("addCache失败", e);
        }

        return flag;
    }

    /**
     * 判断是否存在这个Key
     *
     * @param key
     * @return
     */
    public boolean hasKeys(String key) {
        return cacheMap.containsKey(key);
    }

    /**
     * 获取缓存实体
     */
    public Object getValue(String key) {
        Object ob = cacheMap.get(key);
        if (ob != null) {
            return ob;
        } else {
            return null;
        }
    }

    /**
     * 指定key增加指定的Value
     *
     * @param key
     * @param intValue
     */
    public void incrValue(String key, Integer intValue) {
        Object ob = cacheMap.get(key);
        if (ob != null) {
            Integer value = Integer.parseInt(ob.toString());
            value += intValue;

            cacheMap.put(key, value);
        } else {
            cacheMap.put(key, intValue);
        }
    }

    /**
     * 永久添加使用
     *
     * @param key
     * @param value
     * @return
     */
    public Object getSetValue(String key, Object value) {
        Object ob = cacheMap.get(key);
        if (ob != null) {
            return ob;
        } else {
            addCacheForever(key, value);
            return value;
        }
    }

    /**
     * 有就获取，没有就设置且今天需要过期
     *
     * @param key
     * @param value
     * @return
     */
    public Object getSetValueExpiresToday(String key, Object value) {
        Object ob = cacheMap.get(key);
        if (ob != null) {
            return ob;
        } else {
            addCacheExpiresToday(key, value);
            return value;
        }
    }

    /**
     * 有就获取，没有添加且设置过期时间
     *
     * @param key
     * @param value
     * @param expires
     * @return
     */
    public Object getSetValueExpires(String key, Object value, Date expires) {
        Object ob = cacheMap.get(key);
        if (ob != null) {
            return ob;
        } else {
            addCache(key, value, expires);
            return value;
        }
    }

    /**
     * 获取缓存数据的数量
     *
     * @return
     */
    public int getSize() {
        return cacheMap.size();
    }

    /**
     * 删除缓存
     *
     * @param key
     * @return
     */
    public boolean removeCache(Object key) {
        boolean flag = false;
        try {
            cacheMap.remove(key);
            cacheConfMap.remove(key);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 清除缓存的类
     * 继承Thread线程类
     */
    private static class ClearCache extends Thread {

        public void run() {

            CacheConfig ccm = null;
            Object key = null;
            Set tempSet = null;
            Set set = null;
            while (true) {
                tempSet = new HashSet();
                set = cacheConfMap.keySet();
                Iterator it = set.iterator();
                while (it.hasNext()) {
                    key = it.next();
                    ccm = (CacheConfig) cacheConfMap.get(key);
                    //比较是否需要清除
                    if (!ccm.isForever()) {
                        if ((new Date().getTime() - ccm.getBeginTime()) >= ccm.getDurableTime() * 1000) {
                            //可以清除，先记录下来
                            tempSet.add(key);
                        }
                    }
                }
                //真正清除
                Iterator tempIt = tempSet.iterator();
                while (tempIt.hasNext()) {
                    key = tempIt.next();
                    cacheMap.remove(key);
                    cacheConfMap.remove(key);

                }
//                log.info("Now Caches:" + cacheMap.size());
                //休息
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("ClearCache Time执行异常");
                }
            }
        }
    }


}
