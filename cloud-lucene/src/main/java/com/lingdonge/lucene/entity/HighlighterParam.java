package com.lingdonge.lucene.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class HighlighterParam implements Serializable {

    /**
     * 是否需要设置高亮
     */
    private boolean highlight;

    /**
     * 需要设置高亮的属性名
     */
    private String fieldName;

    /**
     * 高亮前缀
     */
    private String prefix;

    /**
     * 高亮后缀
     */
    private String stuffix;

    /**
     * 显示摘要最大长度
     */
    private int fragmenterLength;

}
