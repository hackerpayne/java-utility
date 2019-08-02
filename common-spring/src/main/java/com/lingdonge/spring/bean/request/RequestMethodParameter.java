package com.lingdonge.spring.bean.request;


import com.lingdonge.core.bean.base.BaseEntity;
import lombok.Data;

/**
 * 参数
 **/
@Data
public class RequestMethodParameter extends BaseEntity {
    /**
     * 参数名
     */
    private String name;

    /**
     * 添加的注解
     */
    private String annoation;

    /**
     * 数据类型
     */
    private String type;

}
