package com.lindonge.core.bean.base;

import lombok.Data;

/**
 * 键值对
 */
@Data
public class ModelPair<T> extends BaseEntity {

    private String key;

    private T value;

    public ModelPair() {

    }

    public ModelPair(String key, T value) {
        this.key = key;
        this.value = value;
    }

}
