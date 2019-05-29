package com.lingdonge.db.db;

import java.io.Serializable;

/**
 * 缓存属性类
 * Created by kyle on 17/5/15.
 */
public class CacheConfig implements Serializable {

    private long beginTime;//缓存开始时间
    private boolean isForever = false;//是否持久
    private int durableTime;//持续时间

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public boolean isForever() {
        return isForever;
    }

    public void setForever(boolean isForever) {
        this.isForever = isForever;
    }

    public int getDurableTime() {
        return durableTime;
    }

    public void setDurableTime(int durableTime) {
        this.durableTime = durableTime;
    }

}
