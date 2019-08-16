package com.lingdonge.spring.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lingdonge.core.bean.base.BaseEntity;
import com.lingdonge.spring.enums.RespStatusEnum;
import lombok.Data;

/**
 * 提供基础字段，包括：message，code
 */
@Data
public class BaseResponse extends BaseEntity {

    /**
     * 返回状态码
     */
    private int code;

    /**
     * 错误码信息说明
     */
    private String msg;

    /**
     * 默认创建成功的回应
     */
    public BaseResponse() {
        this(RespStatusEnum.SUCCESS);
    }

    /**
     *
     * @param responseEnum
     */
    public BaseResponse(RespStatusEnum responseEnum) {
        this(responseEnum.getCode(), responseEnum.getMsg());
    }

    /**
     *
     * @param code
     * @param message
     */
    public BaseResponse(int code, String message) {
        this.code = code;
        this.msg = message;
    }

    /**
     * 判断请求是否成功
     *
     * @return
     */
    @JsonIgnore
    public boolean isSuccess() {
        return this.getCode() == RespStatusEnum.SUCCESS.getCode();
    }

}



