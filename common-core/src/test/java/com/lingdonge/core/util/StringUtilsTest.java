package com.lingdonge.core.util;


import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.lingdonge.core.reflect.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * 字符串工具类单元测试
 *
 * @author Looly
 */
public class StringUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(StringUtilsTest.class);

    @Test
    public void isBlankTest() {
        String blank = "	  　";
        Assert.assertTrue(StringUtils.isBlank(blank));
    }

    @Test
    public void trimTest() {
        String blank = "	 哈哈 　";
        String trim = StringUtils.trim(blank);
        Assert.assertEquals("哈哈", trim);
    }

    @Test
    public void cleanBlankTest() {
        //包含：制表符、英文空格、不间断空白符、全角空格
        String str = "	 你 好　";
        String cleanBlank = StringUtils.cleanBlank(str);
        Assert.assertEquals("你好", cleanBlank);
    }

    @Test
    public void cutTest() {
        String str = "aaabbbcccdddaadfdfsdfsdf0";
        String[] cut = StringUtils.cut(str, 4);
        Console.log(cut);
    }

    @Test
    public void splitTest() {
        String str = "a,b ,c,d,,e";
        List<String> split = StringUtils.split(str, ',', -1, true, true);
        //测试空是否被去掉
        Assert.assertEquals(5, split.size());
        //测试去掉两边空白符是否生效
        Assert.assertEquals("b", split.get(1));


    }

    @Test
    public void testRemoveDuplicateByContains() {
        List<String> list = Lists.newArrayList();
        list.add("www.1.com");
        list.add("www.2.com");
        list.add("www.3.com");
        list.add("www.1.com");
        list.add("www.1.com");

        List<String> listResults = StringUtils.removeDuplicateByContains(list, new String[]{"1.com", "1.com", "1.com"});

        System.out.println("结果数量：" + listResults.size());
        System.out.println(Joiner.on("-------").join(listResults));

    }

}
