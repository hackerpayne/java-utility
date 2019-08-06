package com.lingdonge.core.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * 消费者，处理数据
 */
public class DataEventHandler implements EventHandler<ModelData> {

    @Override
    public void onEvent(ModelData modelData, long l, boolean b) {
        System.out.println("当前线程为:" + Thread.currentThread().getId() + "线程，它处理的数据是：" + modelData.getValue());
    }
}
