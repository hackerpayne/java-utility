package com.lingdonge.core.bean.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 带ID的实体类
 */
@Getter
@Setter
public abstract class BaseIdEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

}
