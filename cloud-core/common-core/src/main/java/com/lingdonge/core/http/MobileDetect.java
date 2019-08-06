package com.lingdonge.core.http;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Kyle on 16/8/23.
 */
@Slf4j
public class MobileDetect {

    private String apiUrl;

    public MobileDetect(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String detectDeviceType() {
        return "";
    }
}
