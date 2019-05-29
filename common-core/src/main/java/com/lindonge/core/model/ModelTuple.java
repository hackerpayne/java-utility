package com.lindonge.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 带Key、Value和附加Boolen的Tuple
 * Created by kyle on 17/5/24.
 */
@Data
public class ModelTuple implements Serializable {

    private static final long serialVersionUID = -1;
    private String key;
    private String value;
    private boolean judge;

    public ModelTuple() {

    }

    public ModelTuple(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public ModelTuple(String key, String value, Boolean judge) {
        this.key = key;
        this.value = value;
        this.judge = judge;
    }

}
