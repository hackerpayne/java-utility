package com.lindonge.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 键值对
 */
@Data
public class ModelPair<T> implements Serializable {

    private static final long serialVersionUID = -1;

    private String key;

    private T value;

    public ModelPair() {

    }

    public ModelPair(String key, T value) {
        this.key = key;
        this.value = value;
    }

}
