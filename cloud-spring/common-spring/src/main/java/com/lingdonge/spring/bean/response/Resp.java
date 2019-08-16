package com.lingdonge.spring.bean.response;

import com.alibaba.fastjson.JSON;
import com.lingdonge.spring.enums.RespStatusEnum;

import java.util.HashMap;

/**
 * 单条记录返回，其实也是支持多条记录的，只是没有强制要求必须是列表数据
 *
 * @param <T>
 */
public class Resp<T> extends BaseResponse {

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    protected T data;

    /**
     *
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Resp() {
        this.data = (T) new HashMap();
    }

    private Resp(RespStatusEnum respStatusEnum) {
        this.setCode(respStatusEnum.getCode());
        this.setMsg(respStatusEnum.getMsg());
    }

    /**
     * 没有错误提示的返回，不建议使用
     *
     * @param <U>
     * @return
     */
    public static <U> Resp<U> fail() {
        return new Resp<U>(RespStatusEnum.FAIL);
    }

    /**
     * 只返回错误提示信息
     *
     * @param message
     * @param <U>
     * @return
     */
    public static <U> Resp<U> fail(String message) {
        Resp<U> resp = new Resp<U>();
        resp.setCode(RespStatusEnum.FAIL.getCode());
        resp.setMsg(message);
        return resp;
    }

    /**
     * 接口调用失败,有错误字符串码和描述,没有返回对象
     *
     * @param code
     * @param message
     * @param <U>
     * @return
     */
    public static <U> Resp<U> fail(int code, String message) {
        Resp<U> resp = new Resp<U>();
        resp.setCode(code);
        resp.setMsg(message);
        return resp;
    }

    /**
     * 直接以枚举返回的信息为准
     *
     * @param respStatusEnum
     * @param <U>
     * @return
     */
    public static <U> Resp<U> fail(RespStatusEnum respStatusEnum) {
        return new Resp<U>(respStatusEnum);
    }

    /**
     * 接口调用失败,有错误字符串码和描述,有返回对象
     *
     * @param code
     * @param message
     * @param data
     * @param <U>
     * @return
     */
    public static <U> Resp<U> fail(int code, String message, U data) {
        Resp<U> resp = new Resp<U>();
        resp.setCode(code);
        resp.setMsg(message);
        resp.setData(data);
        return resp;
    }

    /**
     * 直接返回成功的无数据
     *
     * @param <U>
     * @return
     */
    public static <U> Resp<U> success() {
        return new Resp<U>(RespStatusEnum.SUCCESS);
    }

    /**
     * 以枚举值为准
     *
     * @param respStatusEnum
     * @param <U>
     * @return
     */
    public static <U> Resp<U> success(RespStatusEnum respStatusEnum) {
        return new Resp<U>(respStatusEnum);
    }

    /**
     * 只返回成功的提示信息
     *
     * @param msg
     * @param <U>
     * @return
     */
    public static <U> Resp<U> successWith(String msg) {
        Resp<U> resp = new Resp<U>();
        resp.setCode(RespStatusEnum.SUCCESS.getCode());
        resp.setMsg(msg);
        return resp;
    }

    /**
     * 接口调用成功，有返回对象
     *
     * @param data
     * @param <U>
     * @return
     */
    public static <U> Resp<U> success(U data) {
        Resp<U> resp = new Resp<U>(RespStatusEnum.SUCCESS);
        resp.setData(data);
        return resp;
    }

    /**
     * 自由设置成功信息的成功结果
     *
     * @param msg
     * @param data
     * @param <U>
     * @return
     */
    public static <U> Resp<U> success(String msg, U data) {
        Resp<U> resp = new Resp<U>();
        resp.setCode(RespStatusEnum.SUCCESS.getCode());
        resp.setMsg(msg);
        resp.setData(data);
        return resp;
    }

    @Override
    public String toString() {
        return String.format("Resource { content: %s, %s }", getData(), super.toString());
    }

    /**
     * 直接转换为Json字符串
     *
     * @return
     */
    public String jsonString() {
        return JSON.toJSONString(this);
    }


    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || !obj.getClass().equals(getClass())) {
            return false;
        }

        Resp<?> that = (Resp<?>) obj;

        boolean contentEqual = this.data == null ? that.data == null : this.data.equals(that.data);
        return contentEqual ? super.equals(obj) : false;
    }

    @Override
    public int hashCode() {

        int resultInt = super.hashCode();
        resultInt += data == null ? 0 : 17 * data.hashCode();
        return resultInt;
    }


}
