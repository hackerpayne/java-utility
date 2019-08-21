package com.lingdonge.core.dates;

import org.junit.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;

public class LocalDateUtilTest {

    @Test
    public void testLocalDate() {
        // 获取当前日期
        LocalDate now = LocalDate.now();
        // 设置日期
        LocalDate now2 = LocalDate.of(2099, 2, 28);
        // 解析日期，格式必须是yyyy-MM-dd
        LocalDate now3 = LocalDate.parse("2018-01-12");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String formatRs = now.format(dtf);

        // 取本月第一天
        LocalDate firstDay = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate firstDay2 = now.withDayOfMonth(1);

        // 取本月第2天
        LocalDate secondDay = now.withDayOfMonth(2);
        LocalDate nextMonthDay = now.with(TemporalAdjusters.firstDayOfNextMonth());
        LocalDate nextYearDay = now.with(TemporalAdjusters.firstDayOfNextYear());

        // 明年的这一天
        LocalDate localDate = now.plusYears(1);

        // 当前日期加上往后推20天
        LocalDate plusDate = now.plus(20, ChronoUnit.DAYS);
        LocalDate plusYear = now.plus(10, ChronoUnit.YEARS);

        // 当前日期往前推10天
        LocalDate minusDay = now.minusDays(10);
        LocalDate minusYear = now.minus(10, ChronoUnit.YEARS);

        //localDate转Date
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = now.atStartOfDay(zoneId);
        Instant instant = zdt.toInstant();
        Date fromDate = Date.from(instant);

        // Date转LocalDate
        Date date = new Date();
        Instant instantToUse = date.toInstant();
        ZoneId zoneIdToUse = ZoneId.systemDefault();
        LocalDate localDateToShow = instantToUse.atZone(zoneIdToUse).toLocalDate();

        // 比较日期大小
        boolean b1 = localDateToShow.equals(LocalDate.of(2018, 04, 27));
        boolean b2 = localDateToShow.equals(LocalDate.of(2018, 04, 26));

        // 判断日期前后  -> false
        boolean b3 = localDateToShow.isAfter(LocalDate.of(2018, 04, 26));//false
        boolean b4 = localDateToShow.isAfter(LocalDate.of(2018, 04, 25));//true
        boolean b5 = localDateToShow.isBefore(LocalDate.of(2018, 04, 26));//false
        boolean b6 = localDateToShow.isBefore(LocalDate.of(2018, 04, 25));//false
        boolean b7 = localDateToShow.isBefore(LocalDate.of(2018, 04, 27));//true

        // 计算两个日期之间的时间间隔   格式为：x年x月x天
        Period between = Period.between(localDateToShow, LocalDate.of(2018, 05, 28));
        long bwDays = ChronoUnit.DAYS.between(localDateToShow, LocalDate.of(2018, 05, 28));

    }

    @Test
    public void dateToLocalDate() {
    }

    @Test
    public void dateToLocalDateTime() {
    }

    @Test
    public void localDateToDate() {
    }

    @Test
    public void localDateTimeToDate() {
    }

    @Test
    public void getNowDate() {
    }

    @Test
    public void getNowDate1() {
    }

    @Test
    public void getNowTime() {
    }

    @Test
    public void getYesterdayStr() {
        System.out.println(LocalDateUtil.getYesterdayStr());
    }

    @Test
    public void getYesterday() {
    }

    @Test
    public void getYesterday1() {
    }

    @Test
    public void getTime() {
        System.out.println(LocalDate.parse("2019-08-11").atStartOfDay());
        System.out.println(LocalDateUtil.getTime(LocalDateTime.now().minusHours(1)));
    }

    @Test
    public void fromUnixTime() {
        long time = 1561965215926L;
        long time2 = 1561965215L;
        System.out.println(LocalDateUtil.fromUnixTime(time));
        System.out.println(LocalDateUtil.fromUnixTime(time2));
    }

    @Test
    public void toUnixTime() {
        System.out.println(LocalDateUtil.toUnixTimeShort());
        System.out.println(LocalDateUtil.toUnixTimeShort(LocalDateTime.now()));

        System.out.println(LocalDateUtil.toUnixTime());
        System.out.println(LocalDateUtil.toUnixTime(LocalDateTime.now()));
    }

    @Test
    public void parseStr() {
        LocalDateTime test = LocalDateUtil.parseStr("2019-04-08 23:32:32", DatePattern.DATE_TIME_FORMAT);
        System.out.println(test);
    }

    @Test
    public void getDatesBetweenUsingJava8() {
        LocalDate startDate = LocalDate.parse("2019-04-06");
        LocalDate endDate = LocalDate.now();
        List<LocalDate> betweens = LocalDateUtil.getDatesBetweenUsingJava8(startDate, endDate);
        System.out.println(betweens);
    }

    @Test
    public void betweenTest() {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<String> betweens = LocalDateUtil.getDatesBetween(startDate, endDate);
        System.out.println(betweens);

        betweens = LocalDateUtil.getDatesBetween(startDate, endDate, DatePattern.DATE_SIMPLE_FORMAT);
        System.out.println(betweens);
    }

    @Test
    public void testStartOfDay() {
        System.out.println("1");
        System.out.println(LocalDateUtil.getStartOfDay(LocalDateTime.now()));

        System.out.println("2");
        System.out.println(LocalDateUtil.getStartOfDayStr(LocalDateTime.now()));
    }

    @Test
    public void testEndOfDay() {
        System.out.println("1");
        System.out.println(LocalDateUtil.getEndOfDay(LocalDateTime.now()));

        System.out.println("2");
        System.out.println(LocalDateUtil.getEndOfDayStr(LocalDateTime.now()));
    }

}
