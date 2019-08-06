package com.lingdonge.core.bean.common;

import com.lingdonge.core.bean.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by kyle on 17/5/25.
 */
@Getter
@Setter
public class ModelArticle extends BaseEntity {

    private int artid;

    private String title;

    private String body;

    private String author;

    private String url;

}
