package com.lingdonge.core.dates;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 如果是 Java 8 ，建议使用 DateTimeFormatter 代替 SimpleDateFormat。
 */
public class LocalDateUtil {

    /**
     * Date转LocalDate
     * 1、将java.token.Date转换为ZonedDateTime。
     * 2、使用它的toLocalDate（）方法从ZonedDateTime获取LocalDate。
     *
     * @param dates
     * @return
     */
    public static LocalDate dateToLocalDate(Date dates) {
        return dates.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Date转LocalDateTime
     * 1、从日期获取ZonedDateTime并使用其方法toLocalDateTime（）获取LocalDateTime
     * 2、使用LocalDateTime的Instant（）工厂方法
     *
     * @param dates
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date dates) {

//        Instant instant = dates.toInstant();
//        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
//        return localDateTime;

        return LocalDateTime.ofInstant(dates.toInstant(), ZoneId.systemDefault()); // 使用系统的默认时区。
    }

    /**
     * LocalDate转Date
     * 1、使用ZonedDateTime将LocalDate转换为Instant。
     * 2、使用from（）方法从Instant对象获取Date的实例
     *
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime转Date格式
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取当前时间的，日期yyyy-MM-dd格式
     *
     * @return
     */
    public static String getNowDate() {
        return getNowDate(DatePattern.DATE_FORMAT);
    }

    /**
     * 使用指定格式，生成今天的日期格式
     *
     * @param format
     * @return
     */
    public static String getNowDate(String format) {
        return DateTimeFormatter.ofPattern(format).format(LocalDate.now());
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getNowTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.DATE_TIME_FORMAT));
    }

    /**
     * 获取当前时间的指定格式
     *
     * @param timeFormat
     * @return
     */
    public static String getNowTime(String timeFormat) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeFormat));
    }

    /**
     * 获取LocalDate的普通格式时间，普通格式为：yyyy-MM-dd
     *
     * @param localDate
     * @return
     */
    public static String getDate(LocalDate localDate) {
        return getDate(localDate, "yyyy-MM-dd");
    }

    /**
     * 本地日期转指定格式
     *
     * @param localDate
     * @param pattern
     * @return
     */
    public static String getDate(LocalDate localDate, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(localDate);
    }

    /**
     * 本地时间转普通格式
     *
     * @param localDateTime
     * @return
     */
    public static String localDateTimeToStr(LocalDateTime localDateTime) {
        return localDateTimeToStr(localDateTime, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 本地时间转指定格式
     *
     * @param localDate
     * @param pattern
     * @return
     */
    public static String localDateTimeToStr(LocalDateTime localDate, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(localDate);
    }

    /**
     * 获取昨天的字符串格式
     *
     * @return
     */
    public static String getYesterdayStr() {
        return LocalDate.now().plusDays(-1).format(DateTimeFormatter.ofPattern(DatePattern.DATE_FORMAT));
    }

    /**
     * 获取昨天的时间
     *
     * @return
     */
    public static LocalDate getYesterday() {
        return LocalDate.now().plusDays(-1);
    }

    /**
     * 获取指定日期的昨天
     *
     * @param localDate
     * @return
     */
    public static LocalDate getYesterday(LocalDate localDate) {
        return localDate.plusDays(-1);
    }

    /**
     * @param localDateTime
     * @return
     */
    public static String getTime(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern(DatePattern.DATE_TIME_FORMAT).format(localDateTime);
    }

    /**
     * LocalDateTime转指定格式字符串
     *
     * @param localDateTime
     * @param format
     * @return
     */
    public static String getTime(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }

    /**
     * 获取一天开始时间
     *
     * @param localDateTime
     * @return
     */
    public static LocalDateTime getStartOfDay(LocalDateTime localDateTime) {
        return localDateTime.with(LocalTime.MIN);
    }

    /**
     * 获取一天开始时间
     *
     * @param localDate
     * @return
     */
    public static LocalDateTime getStartOfDay(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    /**
     * 获取一天开始时间
     *
     * @param localDateTime
     * @return
     */
    public static String getStartOfDayStr(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.DATE_TIME_FORMAT);
        return getStartOfDay(localDateTime).format(formatter);
    }

    /**
     * 获取一天开始时间
     *
     * @param localDate
     * @return
     */
    public static String getStartOfDayStr(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.DATE_TIME_FORMAT);
        return getStartOfDay(localDate).format(formatter);
    }

    /**
     * 获取一天结束时间
     *
     * @param localDateTime
     * @return
     */
    public static LocalDateTime getEndOfDay(LocalDateTime localDateTime) {
        return localDateTime.with(LocalTime.MAX);
    }

    /**
     * 获取一天结束时间
     *
     * @param localDate
     * @return
     */
    public static LocalDateTime getEndOfDay(LocalDate localDate) {
        return localDate.atTime(LocalTime.MAX);
    }

    /**
     * 获取一天结束时间
     *
     * @param localDateTime
     * @return
     */
    public static String getEndOfDayStr(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.DATE_TIME_FORMAT);
        return getEndOfDay(localDateTime).format(formatter);
    }

    /**
     * 获取一天结束时间
     *
     * @param localDate
     * @return
     */
    public static String getEndOfDayStr(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.DATE_TIME_FORMAT);
        return getEndOfDay(localDate).format(formatter);
    }

    /**
     * 时间戳转LocalDateTime
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime fromUnixTime(long timestamp) {

        if (String.valueOf(timestamp).length() == 10) { // 适配10位数长度的时间戳
            timestamp = timestamp * 1000;
        }

        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * LocalDateTime转时间戳
     *
     * @param localDateTime
     * @return
     */
    public static long toUnixTime(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * 字符串转LocalDateTime，需要指定时间格式
     *
     * @param time   要处理的时间
     * @param format 时间的应该格式
     * @return
     */
    public static LocalDateTime parseStr(String time, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, df);
    }

    /**
     * 指定时间格式，转为日期
     *
     * @param time
     * @param format
     * @return
     */
    public static LocalDate parseStrToDate(String time, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(time, df);
    }

    /**
     * Java8获取2个日期的日期中间列表
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<LocalDate> getDatesBetweenUsingJava8(LocalDate startDate, LocalDate endDate) {

        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween + 1)
                .mapToObj(i -> startDate.plusDays(i))
                .collect(Collectors.toList());
    }

    /**
     * 获取2个日期之间的值
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static List<String> getDatesBetween(LocalDate beginDate, LocalDate endDate) {
        return getDatesBetween(beginDate, endDate, "");
    }

    /**
     * 获取2个LocalDate之间的每一天
     *
     * @param beginDate
     * @param endDate
     * @param dateFormat 指定显示的格式
     * @return
     */
    public static List<String> getDatesBetween(LocalDate beginDate, LocalDate endDate, String dateFormat) {

        List<String> listDates = Lists.newArrayList();
        Long distance = ChronoUnit.DAYS.between(beginDate, endDate);
        if (distance < 1) {
            return listDates;
        }

        String format = StringUtils.isNotEmpty(dateFormat) ? dateFormat : DatePattern.DATE_FORMAT;

        Stream.iterate(beginDate, dates -> {
            return dates.plusDays(1);
        }).limit(distance + 1).forEach(dates -> {
            listDates.add(DateTimeFormatter.ofPattern(format).format(dates));
        });
        return listDates;
    }


}
