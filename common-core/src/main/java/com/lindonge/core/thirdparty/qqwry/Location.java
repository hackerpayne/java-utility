package com.lindonge.core.thirdparty.qqwry;

import java.io.UnsupportedEncodingException;

/**
 * @Description:ip位置
 * @author:difeng
 * @date:2016年12月13日
 */
public class Location {

    public String ip;

    public String country;

    public String area;

    @Override
    public String toString() {
        return "IP=[" + ip + "];Location [country=" + country + ", area=" + area + "]";
    }

    /**
     * 改变数据的编码
     * @throws UnsupportedEncodingException
     */
    public void changeEnCode() throws UnsupportedEncodingException {
        this.country = new String(this.country.trim().getBytes(), "GBK");
        this.area = new String(this.area.trim().getBytes(), "GBK");
    }

}