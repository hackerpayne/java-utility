package com.lingdonge.core.bean.common;

import com.lingdonge.core.bean.base.BaseEntity;
import lombok.Data;

/**
 * 手机号码归属地查询
 * Created by kyle on 17/5/5.
 */
@Data
public class ModelMobileLocation extends BaseEntity {

    private static final long serialVersionUID = -1;

    private String mobile;
    private String province;
    private String city;
    private String areacode;
    private String zip;
    private String isp;
    private String card;

    public ModelMobileLocation() {

    }

    public ModelMobileLocation(String phone) {
        this.mobile = phone;
    }

}
