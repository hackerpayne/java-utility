package com.lingdonge.core.bean.common;

import com.lingdonge.core.bean.base.BaseEntity;
import lombok.Data;

/**
 * 精确设置每小时的任务信息：用于分小时段设置任务
 * 比如：1点，最小2，最大10个任务
 */
@Data
public class ModelTaskEveryHour extends BaseEntity {

    private int hour;
    private int min;
    private int max;
    private int waitDone;
    private int hasDone;


    /**
     * 默认构造函数
     */
    public ModelTaskEveryHour() {
    }

    /**
     * 构造函数
     *
     * @param hour
     * @param min
     * @param max
     */
    public ModelTaskEveryHour(int hour, int min, int max) {
        this.hour = hour;
        this.max = max;
        this.min = min;
    }


}
