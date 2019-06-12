package com.lingdonge.core.algorithm;

import com.google.common.base.Splitter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DescartesTest {

    @Test
    public static void test1() {
        List<String> list1 = Splitter.on("|").splitToList("全部形式|电影|电视剧|综艺|动画|纪录片|短片");

        List<String> list2 = Splitter.on("|").splitToList("全部类型|剧情|爱情|喜剧|科幻|动作|悬疑|犯罪|恐怖|青春|励志|战争|文艺|黑色幽默|传记|情色|暴力|音乐|家庭");

        List<String> list3 = Splitter.on("|").splitToList("全部地区|大陆|美国|香港|台湾|日本|韩国|英国|法国|德国|意大利|西班牙|印度|泰国|俄罗斯|伊朗|加拿大|澳大利亚|爱尔兰|瑞典|巴西|丹麦");

        List<String> list4 = Splitter.on("|").splitToList("全部特色|经典|冷门佳片|魔幻|黑帮|女性");

        List<List<String>> dimValue = new ArrayList<List<String>>();
        dimValue.add(list1);
        dimValue.add(list2);
        dimValue.add(list3);
        dimValue.add(list4);

        List<List<String>> recursiveResult = new ArrayList<List<String>>();
        // 递归实现笛卡尔积
        Descartes.recursive(dimValue, recursiveResult, 0, new ArrayList<String>());

        System.out.println("递归实现笛卡尔乘积: 共 " + recursiveResult.size() + " 个结果");
        for (List<String> list : recursiveResult) {
            for (String string : list) {
                System.out.print(string + " ");
            }
            System.out.println();
        }

    }

    @Test
    public static void test2() {
        List<String> list1 = new ArrayList<String>();
        list1.add("1");
        list1.add("2");

        List<String> list2 = new ArrayList<String>();
        list2.add("a");
        list2.add("b");

        List<String> list3 = new ArrayList<String>();
        list3.add("3");
        list3.add("4");
        list3.add("5");

        List<String> list4 = new ArrayList<String>();
        list4.add("c");
        list4.add("d");
        list4.add("e");

        List<List<String>> dimValue = new ArrayList<List<String>>();
        dimValue.add(list1);
        dimValue.add(list2);
        dimValue.add(list3);
        dimValue.add(list4);

        List<List<String>> recursiveResult = new ArrayList<List<String>>();
        // 递归实现笛卡尔积
        Descartes.recursive(dimValue, recursiveResult, 0, new ArrayList<String>());

        System.out.println("递归实现笛卡尔乘积: 共 " + recursiveResult.size() + " 个结果");
        for (List<String> list : recursiveResult) {
            for (String string : list) {
                System.out.print(string + " ");
            }
            System.out.println();
        }

        List<List<String>> circulateResult = new ArrayList<List<String>>();
        Descartes.circulate(dimValue, circulateResult);
        System.out.println("循环实现笛卡尔乘积: 共 " + circulateResult.size() + " 个结果");
        for (List<String> list : circulateResult) {
            for (String string : list) {
                System.out.print(string + " ");
            }
            System.out.println();
        }
    }

}