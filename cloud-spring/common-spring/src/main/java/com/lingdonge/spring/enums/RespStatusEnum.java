package com.lingdonge.spring.enums;

/**
 * 请求返回状态枚举类
 */
public enum RespStatusEnum {
    SUCCESS(0, "操作成功"),
    FAIL(1, "操作失败"),

    // Http状态码 4X类
    BAD_REQUEST(400, "404未找到该资源"),
    FORBIDDEN(403, "没有访问权限"),
    PAGE_NOT_FOUND(404, "页面不存在"),
    METHOD_NOT_SUPPORTED(405, "不允许此方法"),

    // HTTP状态码 5X类
    SERVER_ERROR(500, "服务器内部错误"),
    SERVER_BUSY(503, "服务器正忙，请稍后再试!"),

    // 授权类
    NEED_LOGIN(3000, "请先登录"),
    NO_AUTHORIZE(3001, "无权访问页面"),
    SIGNATURE_NOT_MATCH(3002, "请求的签名不匹配"),

    // 参数类
    PARAMATER_ERROR(4000, "参数异常"),
    PARAMATER_ILLEGAL(4001, "参数不合法"),
    NULL(4002, "参数不能为空"),

    ;

    private int code;
    private String msg;

    private RespStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
