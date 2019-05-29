package com.lindonge.core.dates;


/**
 * 常用日期格式
 */
public class DatePattern extends cn.hutool.core.date.DatePattern {

    /**
     * "yyyy-MM-dd格式类型
     * 2017-05-19 这样的固定日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 简化日期格式
     */
    public static final String DATE_SIMPLE_FORMAT = "yyyyMMdd";

    /**
     * "yyyy-MM-dd HH:mm:ss"格式类型
     * 2017-05-19 12:23:33 这样的固定时间格式
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 标准日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss.SSS
     */
    public final static String DATE_TIME_MS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 标准时间格式：HH:mm:ss
     */
    public final static String TIME_FORMAT = "HH:mm:ss";

    /**
     * "yyyy-MM-dd HH:mm"格式类型
     */
    public final static String DATE_TIME_MINUTE_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * "yyyyMMddHHmmss"格式类型
     */
    public final static String DATE_TIME_SHORT_FORMAT = "yyyyMMddHHmmss";

    /**
     * "yyyyMM"格式类型 月份格式
     */
    public final static String MONTH_FORMAT = "yyyyMM";

    /**
     * 常见日期类型数组
     */
    public final static String[] normalDateFormatArray = new String[]{"yyyy-MM-dd", "yyyy/MM/dd", "yyyyMMdd"};

    /**
     * HTTP头中日期时间格式：EEE, dd MMM yyyy HH:mm:ss z
     */
    public final static String HTTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

    /**
     * HTTP头中日期时间格式：EEE MMM dd HH:mm:ss zzz yyyy
     */
    public final static String JDK_DATETIME_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";

    /**
     * 常用日期和时间格式
     */
    public final static String[] NORMAL_PATTERS = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
            "yyyyMMddHHmmss", "yyyyMMdd", "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ss:SSSZZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ss.SSS Z", "dd MMM yyyy"};

}
