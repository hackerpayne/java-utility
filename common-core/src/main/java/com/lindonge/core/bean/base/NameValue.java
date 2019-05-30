package com.lindonge.core.bean.base;

import lombok.Getter;
import lombok.Setter;

/**
 * 名值对(一般用于下拉选)
 * 预留一个拓展字段obj,按需使用
 */
@Getter
@Setter
public class NameValue extends BaseEntity {
    private String name;
    private Object value;
    private Object obj;

    public NameValue() {
    }

    public NameValue(String name, Object value) {
        super();
        this.name = name;
        this.value = value;
    }

    public NameValue(String name, Object value, Object obj) {
        super();
        this.name = name;
        this.value = value;
        this.obj = obj;
    }

}
