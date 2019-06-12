package com.lingdonge.core.enums;

/**
 * 位移枚举操作类：
 * https://www.xuzhengke.cn/?p=577
 * <p>
 * 多选操作会用到两个运算符， & 按位与， | 按位或
 * 这两个符号可以参考在 if 语句中 &&，|| 的使用
 */
public class EnumBinUtil {

    /**
     * 组合结果求个值
     *
     * @param target
     * @return
     */
    public static Integer add(Integer... target) {
        Integer total = 0;
        for (Integer integer : target) {
            total = total | integer;
        }
        return total;
    }

    /**
     * 检查某个值是否在组合结果内
     *
     * @param total
     * @param input
     * @return
     */
    public static Boolean contains(Integer total, Integer input) {
        return (total & input) > 0;
    }

    /**
     * 删除其中一个结果值
     *
     * @param target
     * @param input
     * @return
     */
    public static Integer remove(Integer target, Integer input) {
        return target & ~input;
    }

}
