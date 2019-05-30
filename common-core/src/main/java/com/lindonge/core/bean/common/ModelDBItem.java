package com.lindonge.core.bean.common;

import com.lindonge.core.bean.base.BaseEntity;
import lombok.Data;

/**
 * 插入数据库的实体类整理
 */
@Data
public class ModelDBItem extends BaseEntity {

    private String key;
    private Object value;
    private boolean isUpdate = false;

    public ModelDBItem() {

    }

    public ModelDBItem(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public ModelDBItem(String key, Object value, boolean isUpdate) {
        this.key = key;
        this.value = value;
        this.isUpdate = isUpdate;
    }

}
