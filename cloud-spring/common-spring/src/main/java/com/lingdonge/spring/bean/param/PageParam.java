package com.lingdonge.spring.bean.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 查询条件参数类
 * 请求时：public class LicenceParam extends QueryParam
 */
@ApiModel
public class PageParam extends BaseParam {
    /**
     * 默认页码，第一页
     */
    private static final int DEFAULT_PAGE_NO = 1;
    /**
     * 默认分页大小，默认10条记录
     */
    private static final int DEFAULT_PAGE_SIZE = 10;
    /**
     * 页码
     */
    @ApiModelProperty(value = "第几页")
    private Integer pageNo = DEFAULT_PAGE_NO;
    /**
     * 分页大小
     */
    @ApiModelProperty(value = "每页显示条数")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /**
     * 页码
     */
    public Integer getPageNo() {
        if (this.pageNo == null) {
            return DEFAULT_PAGE_NO;
        }

        return this.pageNo;
    }

    /**
     * 页码
     */
    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * 分页大小
     */
    public Integer getPageSize() {
        if (this.pageSize == null) {
            return DEFAULT_PAGE_SIZE;
        }
        return this.pageSize;
    }

    /**
     * 分页大小
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

}
