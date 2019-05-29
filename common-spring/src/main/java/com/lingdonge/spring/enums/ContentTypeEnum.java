package com.lingdonge.spring.enums;


/**
 * ContentType请求类型，常规下只有4种：
 * 1、Json请求
 * 2、XML请求
 * 3、Form请求
 * 4、MultiPart，带文件时使用这个
 */
public enum ContentTypeEnum {

    JSON("json", "application/json;"),
    XML("xml", "text/xml"),
    FORM("form", "application/x-www-form-urlencoded"),
    MULTIPART("multipart", "multipart/form-data"),
    ;


    private String value;
    private String name;

    ContentTypeEnum(String name, String value) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    /**
     * 获取Key对应的ContentType
     *
     * @param method
     * @return
     */
    public static String getContentType(String method) {
        for (ContentTypeEnum contentType : values()) {
            if (contentType.name().equalsIgnoreCase(method)) {
                return contentType.value;
            }
        }
        return null;
    }

}
