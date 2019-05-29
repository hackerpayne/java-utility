package com.lindonge.core.model;


import com.lindonge.core.algorithm.IWeightRoundRobin;
import lombok.Data;

/**
 * 精确设置每小时的任务信息：用于分小时段设置任务
 * 比如：1点，最小2，最大10个任务
 * Created by kyle on 17/5/5.
 */
@Data
public class ModelTaskEveryHourWeight implements IWeightRoundRobin {

    private int hour;
    private int waitDone;
    private int hasDone;

    // 限制最多数量
    private int maxCountLimit;
    private int weight;

    /**
     * 默认构造函数
     */
    public ModelTaskEveryHourWeight() {
    }

    /**
     * 构造函数
     *
     * @param hour
     * @param weight
     */
    public ModelTaskEveryHourWeight(int hour, int weight) {
        this.hour = hour;
        this.weight=weight;
    }


}
