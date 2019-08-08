package com.lingdonge.crypto.encrypt;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

/**
 * Uuid生成器
 */
public class UuidUtil {

    /**
     * 生成一个随机UUID
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成大写的随机UUID
     *
     * @return
     */
    public static String getUUIDUpperCase() {
        return getUUID().toUpperCase();
    }

    /**
     * 生成随机数
     * @return
     */
    public static String getRadomNumbers() {
        return String.valueOf(new Random().nextInt(899999) + 100000);
    }

    /**
     * 生成一个TraceId，格式为：年月日时分秒加8位随机数
     * 生成结果例：19030514333691807485
     *
     * @return
     */
    public static String getTraceId() {
        return DateTimeFormatter.ofPattern("yyMMddHHmmss").format(LocalDateTime.now()) + RandomStringUtils.randomNumeric(8);
    }

    public static void main(String[] args) {
        System.out.println(UuidUtil.getRadomNumbers());
    }

}
