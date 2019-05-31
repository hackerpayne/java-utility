package com.lingdonge.core.dates;

import org.testng.annotations.Test;

import java.util.Date;

public class DateUtilTest {
    @Test
    public void testNowUnixTimestamp() throws Exception {
    }

    @Test
    public void testGetNowTime1() throws Exception {
    }

    @Test
    public void testGetNowTime2() throws Exception {
    }

    @Test
    public void testGetNowDate() throws Exception {
    }

    @Test
    public void testToLocalTime() throws Exception {
    }

    @Test
    public void testFormatDate() throws Exception {
    }

    @Test
    public void testParseStr() throws Exception {

        Date date = DateUtil.parseStr("2018-11-04", DatePattern.NORMAL_PATTERS);
        System.out.println("解析时间为：" + DateUtil.formatTime(date));

        date = DateUtil.parseStr("2018-11-04 23:93:94", DatePattern.NORMAL_PATTERS);
        System.out.println("解析时间为：" + DateUtil.formatTime(date));

        date = DateUtil.parseStr("20180511233232", DatePattern.NORMAL_PATTERS);
        System.out.println("解析时间为：" + DateUtil.formatTime(date));

        date = DateUtil.parseStr("2018/03/10", DatePattern.NORMAL_PATTERS);
        System.out.println("解析时间为：" + DateUtil.formatTime(date));


    }

    @Test
    public void testToLocalTime1() throws Exception {
    }

    @Test
    public void testToUnixTime() throws Exception {
    }

    @Test
    public void testToUnixTime1() throws Exception {
    }

    @Test
    public void testFormatTime() throws Exception {
    }

    @Test
    public void testParseStringToDate() throws Exception {
    }

    @Test
    public void testDateDiff() throws Exception {
    }

    @Test
    public void testDateDiffSeconds() throws Exception {
    }

    @Test
    public void testDateDiffNextHour() throws Exception {
    }

    @Test
    public void testMillisToNextHour() throws Exception {
    }

    @Test
    public void testDateDiffNextDay() throws Exception {
    }

    @Test
    public void testDateDiff2() throws Exception {
        DateUtil.dateDiff("2017-05-18 13:13:13", "2017-05-18 13:14:13", DatePattern.DATE_TIME_FORMAT);

        int diffNextHourdiffSeconds = DateUtil.dateDiffSeconds("2017-05-18 13:13:13", "2017-05-18 14:14:13", DatePattern.DATE_TIME_FORMAT);

        int diffNextHour = DateUtil.dateDiffNextHour();

        System.out.println(diffNextHour);
    }

    @Test
    public void testGetDateStart() throws Exception {
        System.out.println("一天开始时间");
        System.out.println(DateUtil.formatDate(DateUtil.getDateStart(new Date())));
    }

    @Test
    public void testGetDateEnd() throws Exception {
        System.out.println("一天结束时间");
        System.out.println(DateUtil.formatDate(DateUtil.getDateEnd(new Date())));
    }

    @Test
    public void testGetYesterday() throws Exception {
        System.out.println("昨天时间");
        System.out.println(DateUtil.formatDate(DateUtil.getYesterday()));

        System.out.println("指定日期的昨天：");
        System.out.println(DateUtil.formatDate(DateUtil.getYesterday("2018-02-25", "yyyy-MM-dd")));
    }

    @Test
    public void testGetNowTime() throws Exception {
    }


//    @Test
//    public void dateTest() {
//        long current = DateUtil.current(false);
//        Console.log(current);
//        DateTime date = DateUtil.date(current);
//        Console.log(date);
//    }
//
//    @Test
//    public void nowTest() {
//        // 当前时间
//        Date date = DateUtil.date();
//        Assert.assertNotNull(date);
//        // 当前时间
//        Date date2 = DateUtil.date(Calendar.getInstance());
//        Assert.assertNotNull(date2);
//        // 当前时间
//        Date date3 = DateUtil.date(System.currentTimeMillis());
//        Assert.assertNotNull(date3);
//
//        // 当前日期字符串，格式：yyyy-MM-dd HH:mm:ss
//        String now = DateUtil.now();
//        Assert.assertNotNull(now);
//        // 当前日期字符串，格式：yyyy-MM-dd
//        String today = DateUtil.today();
//        Assert.assertNotNull(today);
//    }
//
//    @Test
//    public void formatAndParseTest() {
//        String dateStr = "2017-03-01";
//        Date date = DateUtil.parse(dateStr);
//
//        String format = DateUtil.format(date, "yyyy/MM/dd");
//        Assert.assertEquals("2017/03/01", format);
//
//        // 常用格式的格式化
//        String formatDate = DateUtil.formatDate(date);
//        Assert.assertEquals("2017-03-01", formatDate);
//        String formatDateTime = DateUtil.formatDateTime(date);
//        Assert.assertEquals("2017-03-01 00:00:00", formatDateTime);
//        String formatTime = DateUtil.formatTime(date);
//        Assert.assertEquals("00:00:00", formatTime);
//    }
//
//    @Test
//    public void beginAndEndTest() {
//        String dateStr = "2017-03-01 22:33:23";
//        Date date = DateUtil.parse(dateStr);
//
//        // 一天的开始
//        Date beginOfDay = DateUtil.beginOfDay(date);
//        Assert.assertEquals("2017-03-01 00:00:00", beginOfDay.toString());
//        // 一天的结束
//        Date endOfDay = DateUtil.endOfDay(date);
//        Assert.assertEquals("2017-03-01 23:59:59", endOfDay.toString());
//    }
//
//    @Test
//    public void offsetDateTest() {
//        String dateStr = "2017-03-01 22:33:23";
//        Date date = DateUtil.parse(dateStr);
//
//        Date newDate = DateUtil.offset(date, DateField.DAY_OF_MONTH, 2);
//        Assert.assertEquals("2017-03-03 22:33:23", newDate.toString());
//
//        //常用偏移
//        DateTime newDate2 = DateUtil.offsetDay(date, 3);
//        Assert.assertEquals("2017-03-04 22:33:23", newDate2.toString());
//        //常用偏移
//        DateTime newDate3 = DateUtil.offsetHour(date, -3);
//        Assert.assertEquals("2017-03-01 19:33:23", newDate3.toString());
//    }


//    @Test
//    public void betweenTest() {
//        String dateStr1 = "2017-03-01 22:34:23";
//        Date date1 = DateUtil.parse(dateStr1);
//
//        String dateStr2 = "2017-04-01 23:56:14";
//        Date date2 = DateUtil.parse(dateStr2);
//
//        long betweenDay = DateUtil.between(date1, date2, DateUnit.DAY);
//        Assert.assertEquals(31, betweenDay);//相差一个月，31天
//
//        long between = DateUtil.between(date1, date2, DateUnit.MS);
//        String formatBetween = DateUtil.formatBetween(between, BetweenFormater.Level.MINUTE);
//        Assert.assertEquals("31天1小时21分", formatBetween);
//    }
//
//    @Test
//    public void timerTest() {
//        TimeInterval timer = DateUtil.timer();
//
//        //---------------------------------
//        //-------这是执行过程
//        //---------------------------------
//
//        timer.interval();//花费毫秒数
//        timer.intervalRestart();//返回花费时间，并重置开始时间
//        timer.intervalMinute();//花费分钟数
//    }
//
//    @Test
//    public void currentTest() {
//        long current = DateUtil.current(false);
//        String currentStr = String.valueOf(current);
//        Assert.assertEquals(13, currentStr.length());
//
//        long currentNano = DateUtil.current(true);
//        String currentNanoStr = String.valueOf(currentNano);
//        Assert.assertNotNull(currentNanoStr);
//    }
}