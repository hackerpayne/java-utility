package com.lindonge.core.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * 在Disruptor实例对象构造时，构造所有缓冲区中的对象实例
 */
public class ModelDataFactory implements EventFactory<ModelData> {
    @Override
    public ModelData newInstance() {
        return new ModelData();
    }
}
