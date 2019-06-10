package com.lingdonge.spring.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BodyValidStatus {
    /**
     * 错误代码
     */
    private String code;
    /**
     * 错误代码解释
     */
    private String message;
    /**
     * 错误字段
     */
    private String field;

}
