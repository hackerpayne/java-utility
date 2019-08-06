package com.lingdonge.spring.restful;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lingdonge.core.page.PageBean;
import com.lingdonge.spring.constant.RespCodeConstant;

import java.util.Collection;
import java.util.List;

/**
 * 继承自ResultSupport，多了meta和data属性，meta用于存放分页相关信息，也可以扩展成其他元数据信息，data用与存放数组信息
 */
public class RespPage<T extends Collection> extends RespSupport {

    public List<?> getResult() {
        return result;
    }

    public void setResult(List<?> result) {
        this.result = result;
    }

    protected List<?> result;

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
        this.setResult(data);
        this.setCode(RespCodeConstant.SUCCESS_CODE);
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
        this.setResult(data);
        this.setCode(RespCodeConstant.SUCCESS_CODE);
    }

    public RespPage(PageBean page) {
        this();
        respMeta.setPageCurrent(page.getCurPage());
        respMeta.setPageSize(page.getPageSize());
        respMeta.setPageCount(page.getTotalPage());
        respMeta.setTotalCount(page.getTotalCount());
        this.setResult(page.getData());
        this.setCode(RespCodeConstant.SUCCESS_CODE);
    }

    /**
     * 返回一个空Fail异常
     *
     * @param <U>
     * @return
     */
    public static <U extends Collection> RespPage<U> fail() {
        RespPage<U> respPage = new RespPage<U>();
        respPage.setCode(RespCodeConstant.FAIL_CODE);
        respPage.setMsg("未知错误");
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
        respPage.setResult(data);
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

    /**
     * 直接返回Page结果，适配MyBatisPlus
     *
     * @param page
     * @param <U>
     * @return
     */
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
