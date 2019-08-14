package com.lingdonge.core.bean.common;

import com.lingdonge.core.bean.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 插入数据库的实体类整理
 */
@Data
@AllArgsConstructor
public class ModelDBItem extends BaseEntity {

    private String key;

    private Object value;

    private boolean isUpdate = false;

}
