package com.lingdonge.db.db;

import com.lingdonge.db.model.BaseEntity;
import lombok.Data;

/**
 * 缓存属性类
 * Created by kyle on 17/5/15.
 */
@Data
public class CacheConfig extends BaseEntity {

    private long beginTime;//缓存开始时间
    private boolean isForever = false;//是否持久
    private int durableTime;//持续时间

}
