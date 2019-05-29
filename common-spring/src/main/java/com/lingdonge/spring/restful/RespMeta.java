package com.lingdonge.spring.restful;

/**
 * 分页头信息，放入meta标签内
 */
public class RespMeta {

    //当前页码
    protected int pageCurrent = 1;

    //当前页记录数量
    protected int pageSize;

    //总页码
    protected int pageCount;

    //总记录数量
    protected int totalCount;

    public int getPageCurrent() {
        return pageCurrent;
    }

    public void setPageCurrent(int pageCurrent) {
        if (pageCurrent <= 0) {
            pageCurrent = 1;
        }
        this.pageCurrent = pageCurrent;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize < 0) {
            pageSize = 0;
        }
        this.pageSize = pageSize;
    }


    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}