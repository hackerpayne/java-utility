package com.lingdonge.spring.bean.response;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lingdonge.core.page.PageBean;
import com.lingdonge.spring.enums.RespStatusEnum;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 继承自ResultSupport，多了meta和data属性，meta用于存放分页相关信息，也可以扩展成其他元数据信息，data用与存放数组信息
 */
public class RespPage<T extends Collection> extends BaseResponse {

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    protected List<?> data;

    private RespMeta respMeta;

    public RespMeta getRespMeta() {
        return respMeta;
    }

    public void setRespMeta(RespMeta respMeta) {
        this.respMeta = respMeta;
    }

    public RespPage() {
        this.respMeta = new RespMeta();
    }

    public RespPage(int pageNo, int pageSize, int pageCount, int totalCount, List<?> data) {
        this();
        respMeta.setPageCurrent(pageNo);
        respMeta.setPageSize(pageSize);
        respMeta.setPageCount(pageCount);
        respMeta.setTotalCount(totalCount);
        this.setData(data);
        this.setCode(RespStatusEnum.SUCCESS.getCode());
        this.setMsg(RespStatusEnum.SUCCESS.getMsg());
    }

    /**
     * 直接返回数据：例：return new RespPage<>(page, this::toSimpleLicenceDTO);
     * @param page
     * @param mapper
     * @param <E>
     */
    public <E> RespPage(IPage<E> page, Function<E, T> mapper) {
        this();
        respMeta.setPageCurrent(page.getCurrent());
        respMeta.setPageSize(page.getSize());
        respMeta.setTotalCount(page.getTotal());
        respMeta.setPageCount(page.getPages());

        if (CollUtil.isEmpty(page.getRecords())) {
            this.setData(Collections.emptyList());
        } else {
            this.setData(page.getRecords().stream().map(mapper).collect(Collectors.toList()));
        }

        this.setCode(RespStatusEnum.SUCCESS.getCode());
        this.setMsg(RespStatusEnum.SUCCESS.getMsg());
    }

    /**
     * 适配MyBatisPlus的Long类型
     *
     * @param pageNo
     * @param pageSize
     * @param pageCount
     * @param totalCount
     * @param data
     */
    public RespPage(Long pageNo, Long pageSize, Long pageCount, Long totalCount, List<?> data) {
        this();
        respMeta.setPageCurrent(pageNo.intValue());
        respMeta.setPageSize(pageSize.intValue());
        respMeta.setPageCount(pageCount.intValue());
        respMeta.setTotalCount(totalCount.intValue());
        this.setData(data);
        this.setCode(RespStatusEnum.SUCCESS.getCode());
        this.setMsg(RespStatusEnum.SUCCESS.getMsg());
    }

    public RespPage(PageBean page) {
        this();
        respMeta.setPageCurrent(page.getCurPage());
        respMeta.setPageSize(page.getPageSize());
        respMeta.setPageCount(page.getTotalPage());
        respMeta.setTotalCount(page.getTotalCount());
        this.setData(page.getData());
        this.setCode(RespStatusEnum.SUCCESS.getCode());
        this.setMsg(RespStatusEnum.SUCCESS.getMsg());
    }

    /**
     * 返回一个空Fail异常
     *
     * @param <U>
     * @return
     */
    public static <U extends Collection> RespPage<U> fail() {
        RespPage<U> respPage = new RespPage<U>();
        respPage.setCode(RespStatusEnum.FAIL.getCode());
        respPage.setMsg(RespStatusEnum.FAIL.getMsg());
        respPage.setRespMeta(null);
        return respPage;
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
    public static <U extends Collection> RespPage<U> fail(int code, String message, List<?> data) {
        RespPage<U> respPage = new RespPage<U>();
        respPage.setCode(code);
        respPage.setMsg(message);
        respPage.setData(data);
        return respPage;
    }

    /**
     * 接口调用失败,有错误字符串码和描述,没有返回对象
     *
     * @param code
     * @param message
     * @param <U>
     * @return
     */
    public static <U extends Collection> RespPage<U> fail(int code, String message) {
        RespPage<U> respPage = new RespPage<U>();
        respPage.setCode(code);
        respPage.setMsg(message);
        respPage.setRespMeta(null);
        return respPage;
    }

    /**
     * 直接返回Page的结果
     *
     * @param page 分页信息
     * @param <U>
     * @return
     */
    public static <U extends Collection> RespPage<U> success(PageBean page) {
        return new RespPage<U>(page);
    }

    public static <U extends Collection> RespPage<U> success(IPage page) {
        return new RespPage<U>(page.getCurrent(), page.getSize(), page.getPages(), page.getTotal(), page.getRecords());
    }

    /**
     * 直接返回Page结果，带List
     *
     * @param page
     * @param dataList
     * @param <U>
     * @return
     */
    public static <U extends Collection> RespPage<U> success(IPage page, List dataList) {
        return new RespPage<U>(page.getCurrent(), page.getSize(), page.getPages(), page.getTotal(), dataList);
    }

    /**
     * 接口调用成功,有返回对象
     *
     * @param pageNo
     * @param pageSize
     * @param totalCount
     * @param data
     * @param <U>
     * @return
     */
    public static <U extends Collection> RespPage<U> success(int pageNo, int pageSize, int pageCount, int totalCount, List<?> data) {
        return new RespPage<U>(pageNo, pageSize, pageCount, totalCount, data);
    }

}
