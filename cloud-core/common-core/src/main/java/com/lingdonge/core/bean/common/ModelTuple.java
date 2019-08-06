package com.lingdonge.core.bean.common;

import com.lingdonge.core.bean.base.BaseEntity;
import lombok.Data;

/**
 * 带Key、Value和附加Boolen的Tuple
 */
@Data
public class ModelTuple extends BaseEntity {

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
