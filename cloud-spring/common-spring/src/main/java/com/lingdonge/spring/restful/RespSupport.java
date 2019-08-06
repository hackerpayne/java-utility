package com.lingdonge.spring.restful;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lingdonge.core.bean.base.BaseEntity;
import com.lingdonge.spring.constant.RespCodeConstant;
import lombok.Data;

/**
 * 提供基础字段，包括：success，message，code
 */
@Data
public class RespSupport extends BaseEntity {

    /**
     * 返回状态码
     */
    private int code;

    /**
     * 错误码信息说明
     */
    private String msg;

    /**
     * 判断请求是否成功
     *
     * @return
     */
    @JsonIgnore
    public boolean isSuccess() {
        return this.getCode() == RespCodeConstant.SUCCESS_CODE;
    }

}



