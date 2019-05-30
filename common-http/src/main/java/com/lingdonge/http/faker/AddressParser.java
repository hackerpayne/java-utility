package com.lingdonge.http.faker;

import com.lindonge.core.bean.common.ModelAddreeDetail;

/**
 * 中文地址解析器
 * http://open.kuaidihelp.com/api/1020 快宝智能解析
 * http://open.kuaidihelp.com/api/1019 快宝地址清洗
 * http://open.kuaidihelp.com/api/1024 快宝智能识别收件人
 */
public class AddressParser {

    /**
     * 地址解析
     *
     * @param address
     * @return
     */
    public static ModelAddreeDetail parseAddress(String address) {
        ModelAddreeDetail addreeDetail = new ModelAddreeDetail();
        addreeDetail.setDetail(address);
        return addreeDetail;
    }
}
