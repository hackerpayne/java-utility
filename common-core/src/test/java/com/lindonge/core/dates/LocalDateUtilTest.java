package com.lindonge.core.dates;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LocalDateUtilTest {

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
        System.out.println(LocalDateUtil.getTime(LocalDateTime.now().minusHours(1)));
    }

    @Test
    public void fromUnixTime() {
    }

    @Test
    public void toUnixTime() {
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
