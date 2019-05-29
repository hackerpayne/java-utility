package com.lindonge.core.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 地址解析结果
 */
@Getter
@Setter
public class ModelAddreeDetail extends BaseEntity {

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 座机号
     */
    private String phone;

    /**
     * 解析详情，原始地址
     */
    private String detail;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 省份
     */
    private String province;

    /**
     * 省别名
     */
    private String provinceAlias;

    /**
     * 城市
     */
    private String city;

    /**
     * 城市别名
     */
    private String cityAlias;

    /**
     * 区或县
     */
    private String district;

    /**
     * 区或县别名
     */
    private String districtAlias;

    /**
     * 地址信息，门牌地址等
     */
    private String address;
}
