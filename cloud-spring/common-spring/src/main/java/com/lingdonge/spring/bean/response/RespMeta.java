package com.lingdonge.spring.bean.response;

/**
 * 分页头信息，放入meta标签内
 */
public class RespMeta {

    //当前页码
    private long pageCurrent = 1;

    //当前页记录数量
    private long pageSize;

    //总页码
    private long pageCount;

    //总记录数量
    private long totalCount;

    public long getPageCurrent() {
        return pageCurrent;
    }

    public void setPageCurrent(long pageCurrent) {
        if (pageCurrent <= 0) {
            pageCurrent = 1;
        }
        this.pageCurrent = pageCurrent;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        if (pageSize < 0) {
            pageSize = 0;
        }
        this.pageSize = pageSize;
    }


    public long getPageCount() {
        return pageCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}