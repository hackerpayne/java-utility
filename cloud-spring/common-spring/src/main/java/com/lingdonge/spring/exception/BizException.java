package com.lingdonge.spring.exception;

import com.lingdonge.spring.enums.RespStatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BizException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = RespStatusEnum.FAIL.getCode();

    public BizException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public BizException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public BizException(int code, String msg) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public BizException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

}