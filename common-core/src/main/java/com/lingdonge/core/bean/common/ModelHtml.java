package com.lingdonge.core.bean.common;


import com.lingdonge.core.bean.base.BaseEntity;
import lombok.Data;

/**
 * 返回HTML结果实体类
 */
@Data
public class ModelHtml extends BaseEntity {

    private String html = "";
    private String error = "";
    private String redirectUrl;

}