package com.lindonge.core.model;

import lombok.Data;

/**
 * IP地址对应的基本信息,实体类,IP地址对应的国家、城市、省、ISP等
 * Created by Kyle on 16/8/29.
 */
@Data
public class ModelIPLocation extends BaseEntity {

    private static final long serialVersionUID = -1;

    /**
     * 获取ISP信息
     */
    private String isp;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 获取国家
     */
    private String country;

    /**
     * 城市
     */
    private String city;

    /**
     * 获取省份
     */
    private String province;

    public ModelIPLocation() {
    }

    public ModelIPLocation(String ip) {
        this.ip = ip;
    }

}
