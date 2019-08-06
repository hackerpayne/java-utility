package com.lingdonge.core.thirdparty.dingding;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Data
public class TextDingMessage implements IDingDingMessage {

    /**
     * 内容
     */
    private String text;

    /**
     * 手机号列表
     */
    private List<String> atMobiles;

    /**
     * 是否所有人收到
     */
    private boolean isAtAll;

    public TextDingMessage(String text) {
        this.text = text;
    }

    @Override
    public String toJsonString() {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("msgtype", "text");

        Map<String, String> textContent = new HashMap<String, String>();
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException("text should not be blank");
        }
        textContent.put("content", text);
        items.put("text", textContent);

        Map<String, Object> atItems = new HashMap<String, Object>();
        if (atMobiles != null && !atMobiles.isEmpty()) {
            atItems.put("atMobiles", atMobiles);
        }
        if (isAtAll) {
            atItems.put("isAtAll", isAtAll);
        }
        items.put("at", atItems);

        return JSON.toJSONString(items);
    }
}
