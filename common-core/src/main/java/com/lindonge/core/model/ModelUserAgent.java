package com.lindonge.core.model;

import com.lindonge.core.algorithm.IWeightRoundRobin;
import lombok.Data;

/**
 * UserAgent实体类,解析UA的设备,操作系统等信息
 * Created by Kyle on 16/8/29.
 */
@Data
public class ModelUserAgent implements IWeightRoundRobin {

    private String ua;

    private String platForm;

    private String browser;

    private String browserVersion;

    private String device;

    private String deviceVersion;

    private String deviceModel;

    private int weight;

    /**
     * 构造函数
     */
    public ModelUserAgent() {
    }

    /**
     * 构造函数
     *
     * @param ua
     */
    public ModelUserAgent(String ua) {
        this.ua = ua;
    }

    public ModelUserAgent(String userAgent, int weight) {
        this.ua = userAgent;
        this.weight = weight;
    }


}
