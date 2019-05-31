package com.lingdonge.core.dates;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.List;


public class JodaDateUtilTest {

    @Test
    public void testDate() {
        String dateStr = LocalDate.now().toString();

        System.out.println(dateStr);
    }

    @Test
    public void testunixTimeStampTo() {
        long unixTimeStamp = 1530115210432L;

        if (String.valueOf(unixTimeStamp).length() > 10) {
            unixTimeStamp = unixTimeStamp / 1000;
        }
        DateTime dateTime = new DateTime(unixTimeStamp * 1000L);

        System.out.println(dateTime.toString(DatePattern.TIME_FORMAT));
    }

    @Test
    public void testParse() {

        DateTime dateTime = null;

        dateTime = JodaUtil.parseStr("2018-11-04");
        System.out.println("解析时间为：" + dateTime.toString(DatePattern.TIME_FORMAT));

        dateTime = JodaUtil.parseStr("2018-11-04 23:93:94");
        System.out.println("解析时间为：" + dateTime.toString(DatePattern.TIME_FORMAT));

        dateTime = JodaUtil.parseStr("20180511233232");
        System.out.println("解析时间为：" + dateTime.toString(DatePattern.TIME_FORMAT));

        dateTime = JodaUtil.parseStr("2018/03/10");
        System.out.println("解析时间为：" + dateTime.toString(DatePattern.TIME_FORMAT));

        dateTime = JodaUtil.parseStr("1530597372");
        System.out.println("解析时间为：" + dateTime.toString(DatePattern.TIME_FORMAT));

        dateTime = JodaUtil.parseStr("1530597372322");
        System.out.println("解析时间为：" + dateTime.toString(DatePattern.TIME_FORMAT));

        dateTime = JodaUtil.parseStr("0130597372322");
        System.out.println("解析时间为：" + dateTime.toString(DatePattern.TIME_FORMAT));

    }

    @Test
    public void testChange() {

        System.out.println(JodaUtil.formatFrom("2018/06/23 14:32:52", "yyyy/MM/dd HH:mm:ss", DatePattern.TIME_FORMAT));

    }


    @Test
    public void getDateBetween() {

        List<LocalDate> betweenDays = JodaUtil.getDateBetween("2018-06-23", "2018-06-28");

        System.out.println(betweenDays);
    }
}
