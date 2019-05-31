package com.lingdonge.core.sys;

import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

/**
 * 控制台操作辅助类
 */
public class ConsoleUtil {

    /**
     * 读取控制台内容
     *
     * @param tip 提示文本
     * @return
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new RuntimeException("请输入正确的" + tip + "！");
    }
}
