package com.lingdonge.spider.faker;

import lombok.extern.slf4j.Slf4j;

/**
 * 生成手机号码段
 */
@Slf4j
public class MobileFacker {

    /**
     * 生成指定长度的号码，前面自动补0
     *
     * @param length
     * @return
     */
//    public List<String> generateNumber(String length) {
//
//    }


    /**
     * 不够位数的在前面补0，保留code的长度位数字
     *
     * @param code
     * @return
     */
    private String autoGenericCode(String code) {
        // 保留code的位数
        return String.format("%0" + code.length() + "d", Integer.parseInt(code) + 1);
    }

    /**
     * 不够位数的在前面补0，保留num的长度位数字
     *
     * @param code
     * @param num
     * @return
     */
    public static String autoGenericCode(String code, int num) {
        // 保留num的位数
        // 0 代表前面补充0
        // num 代表长度为4
        // d 代表参数为正数型
        return String.format("%0" + num + "d", Integer.parseInt(code) + 1);
    }

    /**
     * 使用数字进行补齐
     *
     * @param code
     * @param num
     * @return
     */
    public static String autoGenericCode(Integer code, int num) {
        // 保留num的位数
        // 0 代表前面补充0
        // num 代表长度为4
        // d 代表参数为正数型
        return String.format("%0" + num + "d", code + 1);
    }

}
