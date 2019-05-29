package com.lindonge.core.algorithm;

/**
 * 权重轮询算法的接口类，实现此接口的实体都可以进行权重计算
 * Created by Kyle on 16/12/13.
 */
public interface IWeightRoundRobin {
    public abstract int getWeight();
}
