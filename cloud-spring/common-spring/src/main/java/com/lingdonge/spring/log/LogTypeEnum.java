package com.lingdonge.spring.log;

/**
 * 日志类型
 */
public enum LogTypeEnum {

    BIZ(211, "通用日志"),
    REQ(211, "请求接口报文"),
    RESP(200, "接口响应报文");

    int value;
    String name;

    LogTypeEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getKey() {
        return name;
    }

}
