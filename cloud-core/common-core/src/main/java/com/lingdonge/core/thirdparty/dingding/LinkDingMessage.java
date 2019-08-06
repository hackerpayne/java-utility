package com.lingdonge.core.thirdparty.dingding;


import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 */
@Data
public class LinkDingMessage implements IDingDingMessage {

    private String title;
    private String text;
    private String picUrl;
    private String messageUrl;

    @Override
    public String toJsonString() {
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("msgtype", "link");

        Map<String, String> linkContent = new HashMap<String, String>();
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("title should not be blank");
        }
        linkContent.put("title", title);

        if (StringUtils.isBlank(messageUrl)) {
            throw new IllegalArgumentException("messageUrl should not be blank");
        }
        linkContent.put("messageUrl", messageUrl);

        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException("text should not be blank");
        }
        linkContent.put("text", text);

        if (StringUtils.isNotBlank(picUrl)) {
            linkContent.put("picUrl", picUrl);
        }

        items.put("link", linkContent);

        return JSON.toJSONString(items);
    }
}