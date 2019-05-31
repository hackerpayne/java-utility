package com.lingdonge.core.dates;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.collections.Lists;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 使用Joda进行日期和时间处理
 * http://www.joda.org/joda-time/
 * https://github.com/JodaOrg/joda-time
 * <p>
 * 使用Java8的话，可以不需要再使用这个库
 */
@Deprecated
@Slf4j
public class JodaUtil {

    /**
     * 分钟类型
     */
    public static final int MINITE_TYPE = 0;

    /**
     * 小时类型
     */
    public static final int HOUR_TYPE = 1;

    /**
     * 日类型
     */
    public static final int DAY_TYPE = 2;

    /**
     * 月类型
     */
    public static final int MONTH_TYPE = 3;

    /**
     * 年类型
     */
    public static final int YEAR_TYPE = 4;


    public static void main(String[] args) {

        LocalDate today = LocalDate.now(); // 当前时间
        LocalDate nextWeek = today.plusWeeks(1);// 一周后的日期

        // 带时区的日期和时间
        DateTimeZone zone = DateTimeZone.forID("America/New_York");
        DateTime dateAndTimeInNewYork = new DateTime(null, zone);

        DateTime dateTime = new DateTime(2000, 1, 1, 0, 0, 0, 0);

        System.out.println(dateTime.plusDays(90).toString("E MM/dd/yyyy HH:mm:ss.SSS")); // 指定时间加90天

        /**
         * 日期前后判断
         */
        LocalDate tomorrow = new LocalDate(2016, 11, 19);
        if (tomorrow.isAfter(today)) {
            System.out.println("Tomorrow comes after today");
        }
        LocalDate yesterday = today.minusDays(1); //昨天的日期
        if (yesterday.isBefore(today)) {
            System.out.println("Yesterday is day before today");
        }

        // 时间增减
        System.out.println(dateTime.plusDays(45).plusMonths(1).dayOfWeek().withMaximumValue().toString("E MM/dd/yyyy HH:mm:ss.SSS"));//距离 Y2K 45 天之后的某天在下一个月的当前周的最后一天的日期
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getTime() {
        return getTime("");
    }

    /**
     * 获取当前时间
     *
     * @param dateFormat
     * @return
     */
    public static String getTime(String dateFormat) {
        if (StringUtils.isEmpty(dateFormat)) {
            return DateTime.now().toString(DatePattern.DATE_TIME_FORMAT);
        }
        return DateTime.now().toString(dateFormat);
    }

    /**
     * 获取当前时间的日期格式
     *
     * @return
     */
    public static String getDate() {
        return getDate("");
    }

    /**
     * 返回当前日期 yyyy-MM-dd格式
     *
     * @return
     */

    /**
     * 返回当前时间的日期，可以指定格式，不指定返回 yyyy-MM-dd格式
     *
     * @param dateFormat
     * @return
     */
    public static String getDate(String dateFormat) {
        if (StringUtils.isEmpty(dateFormat)) {
            return LocalDate.now().toString(DatePattern.DATE_TIME_FORMAT);
        }

        return LocalDate.now().toString(dateFormat);
    }

    /**
     * 获取昨天的日期格式
     *
     * @return
     */
    public static String getYesterdayDate(String dateFormat) {
        if (StringUtils.isEmpty(dateFormat)) {
            return LocalDate.now().minusDays(1).toString(); //昨天的日期
        }
        return LocalDate.now().minusDays(1).toString(dateFormat);
    }

    /**
     * 获取指定日期的昨天
     *
     * @param dateStr
     * @return
     */
    public static Date getYesterday(String dateStr) {
        return getYesterday(dateStr, DatePattern.DATE_FORMAT);
    }

    /**
     * 获取指定日期的昨天
     *
     * @param dateStr
     * @param dateFormat
     * @return
     */
    public static Date getYesterday(String dateStr, String dateFormat) {
        Date tempDate = parseStr(dateStr, dateFormat);
        return DateUtil.getYesterday(tempDate);
    }

    /**
     * 把字符串解析为日期格式，兼容各种格式
     *
     * @param input
     * @return
     */
    public static String parseStrToDate(String input) {
        DateTime dateTime = parseStr(input);
        if (null != dateTime) {
            return dateTime.toString(DatePattern.DATE_FORMAT);
        }
        return null;
    }

    /**
     * 将字符串转换为Date
     *
     * @param input   输入字符串
     * @param pattern 字符串的格式，第一个参数的格式要符合第二个参数
     * @return java.token.Date
     */
    public static Date parseStr(String input, String pattern) {
        Preconditions.checkNotNull(input);
        Preconditions.checkNotNull(pattern);
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        DateTime dateTime = DateTime.parse(input, formatter);
        return dateTime.toDate();
    }

    /**
     * 解析Unix时间戳
     *
     * @param unixTimeStamp
     * @return
     */
    public static DateTime getUnixTimeStamp(Long unixTimeStamp) {

        if (String.valueOf(unixTimeStamp).length() > 10) {
            unixTimeStamp = unixTimeStamp / 1000;
        }
        try {
            return new DateTime(unixTimeStamp * 1000L);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Uninx时间戳转换为指定格式，线程安全，指定格式
     *
     * @param unixTimeStamp Uninx时间戳，支持10和13位2种格式
     * @param formatPattern 为空时使用时间格式
     * @return
     */
    public static String unixTimeStampTo(Long unixTimeStamp, String formatPattern) {
        if (String.valueOf(unixTimeStamp).length() > 10) {
            unixTimeStamp = unixTimeStamp / 1000;
        }
        DateTime dateTime = new DateTime(unixTimeStamp * 1000L);

        if (StringUtils.isEmpty(formatPattern)) {
            formatPattern = DatePattern.DATE_TIME_FORMAT;
        }
        return dateTime.toString(formatPattern);
    }

    /**
     * 将日期从fromPattern转换为toPattern
     *
     * @param dateStr     日期字符串
     * @param fromPattern 原始格式，比如yyyy-MM-dd HH:mm:ss
     * @param toPattern   返回格式，比如 yyyy-MM-dd
     * @return
     */
    public static String formatFrom(String dateStr, String fromPattern, String toPattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(fromPattern);
        DateTime localTime = dateTimeFormatter.parseDateTime(dateStr);
        return localTime.toString(toPattern);
    }

    /**
     * 使用常规格式解析日期和时间
     *
     * @param input
     * @return
     */
    public static DateTime parseStr(String input) {
        DateTime dateTime = parseStr(input, DatePattern.NORMAL_PATTERS);
        if (null == dateTime || dateTime.getYear() < 1980) {
            dateTime = getUnixTimeStamp(NumberUtils.toLong(input));
        }

        // 如果最后解析出来还是1900年之前的话，直接干掉不要，应该是Unix解析出错了
        if (null != dateTime && dateTime.getYear() < 1980) {
            dateTime = null;
        }

        return dateTime;
    }

    /**
     * 把字符串按照多种形式进行解析，并转换为Joda的时间格式
     *
     * @param input         日期字符串
     * @param parsePatterns 多种不同的日期格式
     * @return
     */
    public static DateTime parseStr(String input, String[] parsePatterns) {

        DateTime dateTime = null;
        try {
            Date date = DateUtils.parseDate(input, parsePatterns);
            dateTime = new DateTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTime;
    }

    /**
     * 获取2个时间的日期差
     *
     * @param start
     * @param end
     * @return
     */
    public static Integer getDaysBetween(String start, String end) {

        DateTime startTime = parseStr(start);
        DateTime endTime = parseStr(end);

        Days betweenDays = Days.daysBetween(startTime, endTime);
        return betweenDays.getDays();
    }

    /**
     * 取2个时间范围内的每一天
     *
     * @param start
     * @param end
     * @return
     */
    public static List<LocalDate> getDateBetween(String start, String end) {

        List<LocalDate> listDates = Lists.newArrayList();

        DateTime startTime = parseStr(start);
        DateTime endTime = parseStr(end);

        Integer betweenDays = Days.daysBetween(startTime, endTime).getDays();
        if (betweenDays > 0) {
            for (int i = 0; i <= betweenDays; i++) {
                listDates.add(startTime.plusDays(i).toLocalDate());
            }
        }
        return listDates;
    }

    /**
     * 判断时间是否在指定范围内。
     *
     * @param start
     * @param end
     * @param target
     * @return
     */
    public static boolean isBetweenInclusive(LocalDate start, LocalDate end, LocalDate target) {
        return !target.isBefore(start) && !target.isAfter(end);
    }

    /**
     * 判断时间是否在指定范围内
     *
     * @param dateStr
     * @param start
     * @param end
     * @return
     */
    public Boolean inRange(String dateStr, String start, String end) {
        DateTime startTime = parseStr(start);
        DateTime endTime = parseStr(end);
        DateTime dateJudge = parseStr(dateStr);

        Interval interval = new Interval(startTime, endTime);
        return interval.contains(dateJudge);
    }

}
