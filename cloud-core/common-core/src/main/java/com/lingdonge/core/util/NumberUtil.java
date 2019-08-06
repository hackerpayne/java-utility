package com.lingdonge.core.util;

import org.apache.commons.lang3.math.NumberUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * 数字工具类
 */
public class NumberUtil extends NumberUtils {


    private NumberUtil() {
    }

    /**
     * 保留小数位，采用四舍五入
     *
     * @param number 被保留小数的数字
     * @param digit  保留的小数位数
     * @return 保留小数后的字符串
     */
    public static String roundStr(double number, int digit) {
        return String.format("%." + digit + 'f', number);
    }

    /**
     * A除B，保留2位小数
     *
     * @param a
     * @param b
     * @return
     */
    public static String getRate(double a, double b) {
        DecimalFormat df = new DecimalFormat("0.00");
        if (b != 0) {
            return df.format(a / b);
        }
        return null;
    }

    /**
     * 获取百分比结果
     *
     * @param a
     * @param b
     * @return
     */
    public static String getPercent(double a, double b) {
        NumberFormat nt = NumberFormat.getPercentInstance();//获取格式化对象
        nt.setMinimumFractionDigits(2);// 设置百分数精确度2即保留两位小数
        return nt.format(a / b); // 最后格式化并输出
    }

    /**
     * Double转换为指定的精度
     * 比如：formatDouble(116.46604901357878,5) 结果会只保留5位小数
     *
     * @param data
     * @param length
     * @return
     */
    public static double formatDouble(double data, Integer length) {
        NumberFormat ddf1 = NumberFormat.getNumberInstance();
        ddf1.setMaximumFractionDigits(length);
        String convResult = ddf1.format(data);
        return toDouble(convResult);
    }


    public static int compareLong(long o1, long o2) {
        if (o1 < o2) {
            return -1;
        } else if (o1 == o2) {
            return 0;
        } else {
            return 1;
        }
    }

}
