package com.lingdonge.core.thirdparty.dingding;


import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Getter
@Setter
public class SendResult {
    private boolean isSuccess;
    private Integer errorCode;
    private String errorMsg;

    public SendResult() {
        this.isSuccess = false;
        this.errorCode = 0;
    }

    @Override
    public String toString() {
        Map<String, Object> items = new HashMap<>();
        items.put("errorCode", errorCode);
        items.put("errorMsg", errorMsg);
        items.put("isSuccess", isSuccess);
        return JSON.toJSONString(items);
    }
}