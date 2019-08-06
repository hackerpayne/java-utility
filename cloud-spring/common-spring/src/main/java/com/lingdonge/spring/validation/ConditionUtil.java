package com.lingdonge.spring.validation;

import java.util.List;

/**
 * 检查查询条件
 */
public class ConditionUtil {

    /**
     * 检测String 非空且length大于0为真
     **/
    public static boolean checkString(String string) {
        return string != null && string.trim().length() > 0;
    }

    /**
     * 检测Integer 非空且大于0为真
     **/
    public static boolean checkInteger(Integer integer) {
        return integer != null && integer > 0;
    }

    /**
     * 检测Float 非空且大于0为真
     **/
    public static boolean checkFloat(Float f) {
        return f != null && f > 0;
    }

    /**
     * 检测Long 非空且大于0为真
     **/
    public static boolean checkLong(Long l) {
        return l != null && l > 0;
    }

    /**
     * 检查String数组 是否这个长度 多数用于日期
     **/
    public static boolean checkStringArray(String[] strarray, Integer arraylength) {
        return checkObjectArray(strarray, arraylength);
    }

    /**
     * 检查String数组 是否这个长度 多数用于日期
     *
     * @param strarray
     * @param arraylength
     * @return
     */
    public static boolean checkObjectArray(Object[] strarray, Integer arraylength) {
        if (strarray == null) {
            return false;
        }
        if (strarray.length != arraylength) {
            return false;
        }
        for (int i = 0; i < arraylength; i++) {
            if (strarray[i] != null && strarray[i] instanceof String && ((String) strarray[i]).trim().length() == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Integer[]转换成以以某个符号为连接的字符串
     **/
    public static String convertoString(Object[] objarray, String join) {
        StringBuilder str = new StringBuilder();
        if (objarray != null && objarray.length > 0) {
            for (Object o : objarray) {
                str.append(o.toString() + join);
            }
            return str.toString().substring(0, str.toString().length() - 1);
        }
        return str.toString();
    }

    /**
     * List<String 或者Int >转换成以以某个符号为连接的字符串
     **/
    public static String convertoString(List<?> objarray, String join) {
        StringBuilder str = new StringBuilder();
        if (objarray != null && objarray.size() > 0) {
            for (Object o : objarray) {
                str.append("'" + o.toString() + "'" + join);
            }
            return str.toString().substring(0, str.toString().length() - 1);
        }
        return str.toString();
    }

    /*** 检测数组 用于Hibernate原有in语句转换 ***/
    public static boolean checkInArray(String a[]) {
        return a != null && a.length >= 1;
    }

    /**
     * 检测Double 非空且大于0为真
     *
     * @param value
     * @return
     */
    public static boolean checkDouble(Double value) {
        return value != null && value > 0;
    }

    /**
     * 检测List 非空且大于0为真
     **/
    public static boolean checkList(List<?> list) {
        return list != null && list.size() > 0;
    }

    private ConditionUtil() {
        // 禁止实例化
    }
}
