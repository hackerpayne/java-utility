package com.lingdonge.spring.bean.response;

import com.lingdonge.spring.enums.RespStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用返回结果类
 *
 * @param <T>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommonResponse<T> extends BaseResponse {
    /**
     * 数据列表
     */
    protected T data;

    public CommonResponse() {
        super();
    }

    public CommonResponse(T data) {
        super();
        this.data = data;
    }

    /**
     * @param respStatusEnum
     * @param data
     */
    public CommonResponse(RespStatusEnum respStatusEnum, T data) {
        super(respStatusEnum);
        this.data = data;
    }

    /**
     * 不带数据的返回
     *
     * @param respStatusEnum
     */
    public CommonResponse(RespStatusEnum respStatusEnum) {
        super(respStatusEnum);
    }

}
