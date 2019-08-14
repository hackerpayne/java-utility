package com.lingdonge.spring.enums;


import com.lingdonge.core.bean.base.NameValue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static String getContentType(String name) {
        Optional<ContentTypeEnum> optional = Stream.of(values()).filter(item -> item.name.equals(name)).findAny();
        return optional.map(ContentTypeEnum::getValue).orElse(null);
    }

    public static ContentTypeEnum getItem(Integer value) {
        return Stream.of(values()).filter(item -> item.value.equals(value)).findAny().orElse(null);
    }

    public static List<NameValue> getItemList() {
        return Arrays.stream(values()).map(item -> new NameValue(item.name, item.value)).collect(toList());
    }

}
