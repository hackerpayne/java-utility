package com.lingdonge.core.dates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.lingdonge.core.captcha.RandomValidateCodeUtil;
import org.apache.commons.lang3.time.DateUtils;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 时间和日期操作的一些常用辅助类
 * SimpleDateFormat 是线程不安全的类，多线程环境下注意线程安全问题
 *
 * 旧版本建议不再使用
 */
@Deprecated
@Slf4j
public class DateUtil {

    /**
     * 生成当前Unix时间戳
     *
     * @return
     */
    public static long nowUnixTimestamp() {

//        // 方法一：
//        Calendar cal = Calendar.getInstance();
//        return cal.getTimeInMillis() / 1000;

        // 方法二：
        //return new Date().getTime();
        return System.currentTimeMillis() / 1000;
    }

    // 线程安全的DateTime
    private static final ThreadLocal<SimpleDateFormat> threadLocalSDFTime = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DatePattern.DATE_TIME_FORMAT);
        }
    };

    private static final ThreadLocal<SimpleDateFormat> threadLocalSDFDate = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DatePattern.DATE_FORMAT);
        }
    };

    /**
     * 获取当前时间，格式为：yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getNowTime() {
        return threadLocalSDFTime.get().format(new Date());// new Date()为获取当前系统时间
    }

    /**
     * 获取当前时间的指定格式
     *
     * @param timeFormat
     * @return
     */
    public static String getNowTime(String timeFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePattern.DATE_TIME_FORMAT);
        return simpleDateFormat.format(new Date());// new Date()为获取当前系统时间
    }

    /**
     * 获取当前时间的，日期yyyy-MM-dd格式
     *
     * @return
     */
    public static String getNowDate() {
        return threadLocalSDFDate.get().format(new Date());// new Date()为获取当前系统时间
    }

    /**
     * 使用指定格式，生成今天的日期格式
     *
     * @param format
     * @return
     */
    public static String getNowDate(String format) {
        return threadLocalSDFDate.get().format(new Date());// new Date()为获取当前系统时间
    }

    /**
     * 10位Unix时间戳转换为yyyy-MM-dd HH:mm:ss时间格式
     *
     * @param unix
     * @return
     */
    public static String toLocalTime(String unix) {
        return toLocalTime(unix, DatePattern.DATE_TIME_FORMAT);
    }

    /**
     * 转换为时间格式
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static String formatTime(Date date) throws ParseException {
        return threadLocalSDFTime.get().format(date);
    }

    /**
     * 转换Date为日期格式
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static String formatDate(Date date) throws ParseException {
        return threadLocalSDFDate.get().format(date);
    }

    /**
     * 解析字符串日期为Date格式
     *
     * @param strDate
     * @return
     * @throws ParseException
     */
    public static Date parseStrToDate(String strDate) throws ParseException {
        return threadLocalSDFDate.get().parse(strDate);
    }

    /**
     * 字符串解析为时间
     *
     * @param strDate
     * @return
     * @throws ParseException
     */
    public static Date parseStrToTime(String strDate) throws ParseException {
        return threadLocalSDFTime.get().parse(strDate);
    }

    /**
     * 10位Unix时间戳转换为指定格式
     *
     * @param unix   要转换的Unix时间
     * @param format 指定的转换格式
     * @return
     */
    public static String toLocalTime(String unix, String format) {

        Long timestamp;
        if (unix.length() == 10) {
            timestamp = Long.parseLong(unix) * 1000;
        } else {
            timestamp = Long.parseLong(unix);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new java.util.Date(timestamp));
    }


    /**
     * 把yyyy-MM-dd HH:mm:ss时间格式转换为Unix时间戳
     *
     * @param local
     * @return
     */
    public static String toUnixTime(String local) {
        return toUnixTime(local, DatePattern.DATE_TIME_FORMAT);
    }

    /**
     * 指定格式的时间转换为Unix时间戳
     *
     * @param local
     * @param format
     * @return
     */
    public static String toUnixTime(String local, String format) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        String unix = "";
        try {
            unix = simpleDateFormat.parse(local).getTime() + "";
        } catch (ParseException e) {
            log.error("DateUtil toUnixTime 时间格式有误", e);
        }
        return unix;
    }

    /**
     * 毫秒转为：x天x小时x分钟x秒
     *
     * @param ms
     * @return
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
        if (milliSecond > 0) {
            sb.append(milliSecond + "毫秒");
        }
        return sb.toString();
    }


    /**
     * 计算2个时间相关了多少毫秒
     *
     * @param startTime
     * @param endTime
     * @param format
     * @return
     * @throws ParseException
     */
    public static long dateDiff(String startTime, String endTime, String format) throws ParseException {
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
        return diff;
    }

    /**
     * 计算2个时间相差了多少秒
     *
     * @param startTime
     * @param endTime
     * @param format
     * @return
     * @throws ParseException
     */
    public static int dateDiffSeconds(String startTime, String endTime, String format) throws ParseException {
        long diff = dateDiff(startTime, endTime, format);
        return (int) (diff / 1000);
    }

    /**
     * 现在离下个小时，还有多少秒
     *
     * @return
     */
    public static int dateDiffNextHour() {

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        SimpleDateFormat df = new SimpleDateFormat(DatePattern.DATE_TIME_FORMAT);

        long diff = cal.getTimeInMillis() - SystemClock.now();

        return (int) (diff / 1000);
    }

    /**
     * 获取2个日期中间的每一天
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Date> getDatesBetweenUsingJava7(Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    /**
     * 离下个小时还有多少毫秒
     *
     * @return
     */
    public static long millisToNextHour() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);
        int minutesToNextHour = 60 - minutes;
        int secondsToNextHour = 60 - seconds;
        int millisToNextHour = 1000 - millis;
        return minutesToNextHour * 60 * 1000 + secondsToNextHour * 1000 + millisToNextHour;
    }


    /**
     * 离明天，还有多少秒
     *
     * @return
     */
    public static int dateDiffNextDay() {

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        long diff = cal.getTimeInMillis() - SystemClock.now();

        return (int) (diff / 1000);
    }

    /**
     * Date转换为Calendar对象
     *
     * @param inputDate
     * @return
     */
    public static Calendar dateToCalendar(Date inputDate) {
        return DateUtils.toCalendar(inputDate);
    }

    /**
     * 计算时间差
     *
     * @param startTime
     * @param endTime
     * @param format
     * @throws ParseException
     */
    public static void dateDiff2(String startTime, String endTime, String format) throws ParseException {

//按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
        long nh = 1000 * 60 * 60;//一小时的毫秒数
        long nm = 1000 * 60;//一分钟的毫秒数
        long ns = 1000;//一秒钟的毫秒数long diff;

        //获得两个时间的毫秒时间差异
        long diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
        long day = diff / nd;//计算差多少天
        long hour = diff % nd / nh;//计算差多少小时
        long min = diff % nd % nh / nm;//计算差多少分钟
        long sec = diff % nd % nh % nm / ns;//计算差多少秒

        // 输出结果
        System.out.println("时间相差：" + day + "天" + hour + "小时" + min + "分钟" + sec + "秒。");

    }

    /**
     * 获取一天的结束时间点
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date getDateStart(Date date) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取指定日期，指定格式的开始时间
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getDateStartString(Date date) throws ParseException {
        return threadLocalSDFTime.get().format(getDateStart(date));
    }

    /**
     * 获取一天的结束时间点：23分59秒59毫秒
     *
     * @param date
     * @return
     */
    public static Date getDateEnd(Date date) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * 获取日期结束的时间格式
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getDateEndString(Date date) throws ParseException {
        return threadLocalSDFTime.get().format(getDateEnd(date));
    }

    /**
     * 获取昨天的日期格式
     *
     * @return
     */
    public static Date getYesterday() {
        return getYesterday(new Date());
    }

    public static String getYesterdayStr() {
        return threadLocalSDFDate.get().format(getYesterday());
    }

    /**
     * 获取指定日期的昨天
     *
     * @param dates
     * @return
     */
    public static Date getYesterday(Date dates) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dates);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

    public static String getYesterdayStr(Date dates) {
        return threadLocalSDFDate.get().format(getYesterday(dates));
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


    public static String getYesterdayStr(String dates) {
        return threadLocalSDFDate.get().format(getYesterday(dates));
    }

    /**
     * 获取指定日期的昨天
     *
     * @param dateStr
     * @param dateFormat
     * @return
     */
    public static Date getYesterday(String dateStr, String dateFormat) {
        Date tempDate = parseStr(dateStr, new String[]{dateFormat});
        return getYesterday(tempDate);
    }

    /**
     * 过去七天
     *
     * @return
     */
    public static Date getLast7Day() {
        return getLast7Day(new Date());
    }

    /**
     * 获取指定日期的7天前
     *
     * @param dates
     * @return
     */
    public static Date getLast7Day(Date dates) {
        Calendar calendar = Calendar.getInstance();
        //过去七天
        calendar.setTime(dates);
        calendar.add(Calendar.DATE, -7);
        return calendar.getTime();
    }

    /**
     * 一个月前的今天
     *
     * @return
     */
    public static Date getLastMonth() {
        return getLastMonth(new Date());
    }

    /**
     * 获取指定日期的1个月前
     *
     * @param dates
     * @return
     */
    public static Date getLastMonth(Date dates) {
        Calendar calendar = Calendar.getInstance();

        //过去一月
        calendar.setTime(dates);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    /**
     * 3个月前的今天
     *
     * @return
     */
    public static Date getLast3Month() {
        return getLast3Month(new Date());
    }

    /**
     * 获取指定日期的3个月前日期
     *
     * @param dates
     * @return
     */
    public static Date getLast3Month(Date dates) {
        Calendar calendar = Calendar.getInstance();

        //过去三个月
        calendar.setTime(dates);
        calendar.add(Calendar.MONTH, -3);
        return calendar.getTime();
    }

    /**
     * 去年今天
     *
     * @return
     */
    public static Date getLastYear() {
        return getLastYear(new Date());
    }

    /**
     * 获取指定日期的一年前
     *
     * @param dates
     * @return
     */
    public static Date getLastYear(Date dates) {
        Calendar calendar = Calendar.getInstance();

        //过去一年
        calendar.setTime(dates);
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
    }

    /**
     * 获取范围之间的日期格式：比如2017-01-01至2017-01-10将会取得一个10个数组
     * 包括前后2个日期
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static List<String> getDatesBetween(String beginDate, String endDate) {
        Calendar cal = Calendar.getInstance();

        List<String> listDates = Lists.newArrayList();
        try {
            Date startDates = parseStrToDate(beginDate);
            Date endDates = parseStrToDate(endDate);

            cal.setTime(startDates);
            while (cal.getTime().before(endDates)) {
                listDates.add(formatDate(cal.getTime()));
                cal.add(Calendar.DATE, 1);
            }

            listDates.add(formatDate(cal.getTime()));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return listDates;

    }

    /**
     * 把字符串按照多种形式进行解析
     *
     * @param input         日期字符串
     * @param parsePatterns 多种不同的日期格式
     * @return
     */
    public static Date parseStr(String input, String[] parsePatterns) {

        Date date = null;
        try {
            date = DateUtils.parseDate(input, parsePatterns);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    public static String formatterDate(String dateText) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = formatter.parse(dateText);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = sdf.format(date);
        return sDate;
    }

    public static String formatterDate(Date date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = sdf.format(date);
        return sDate;
    }

    /**
     * 获得指定日期的前一天
     *
     * @param specifiedDay
     * @return
     * @throws Exception
     */
    public static String getSpecifiedDayBefore(String specifiedDay) {
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayBefore;
    }

    /**
     * 获得指定日期的后一天
     *
     * @param specifiedDay
     * @return
     */
    public static String getSpecifiedDayAfter(String specifiedDay) {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + 1);

        String dayAfter = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayAfter;
    }

}
