package com.lingdonge.core.util;

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

    public static void main(String[] args) {

        Integer check1 = 1 << 0; // (2)进制：0000 0001, (10)进制：1
        Integer check2 = 1 << 1; // (2)进制：0000 0010, (10)进制：2
        Integer check3 = 1 << 2; // (2)进制：0000 0100, (10)进制：4
        Integer check4 = 1 << 3; // (2)进制：0000 1000, (10)进制：8
        System.out.println(check1);
        System.out.println(check2);
        System.out.println(check3);
        System.out.println(check4);

        Integer total = EnumBinUtil.add(check1, check2, check4);
        System.out.println("计算组合1：" + total);

        System.out.println("计算包含1：" + EnumBinUtil.contains(total, check1));
        System.out.println("计算包含2：" + EnumBinUtil.contains(total, check2));
        System.out.println("计算包含3：" + EnumBinUtil.contains(total, check3));
        System.out.println("计算包含4：" + EnumBinUtil.contains(total, check4));

        total = EnumBinUtil.remove(total, check2);
        System.out.println("删除其中一个之后：" + total);

        System.out.println("计算包含1：" + EnumBinUtil.contains(total, check1));
        System.out.println("计算包含2：" + EnumBinUtil.contains(total, check2));
        System.out.println("计算包含3：" + EnumBinUtil.contains(total, check3));
        System.out.println("计算包含4：" + EnumBinUtil.contains(total, check4));
    }
}
