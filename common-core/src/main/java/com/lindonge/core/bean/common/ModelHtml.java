package com.lindonge.core.bean.common;


import com.lindonge.core.bean.base.BaseEntity;
import lombok.Data;

/**
 * 返回HTML结果实体类
 */
@Data
public class ModelHtml extends BaseEntity {

    private static final long serialVersionUID = -1;

    private String html = "";
    private String error = "";
    private String RedirectUri;


}