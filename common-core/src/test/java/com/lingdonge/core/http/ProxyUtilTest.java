package com.lingdonge.core.http;

import org.junit.Test;

public class ProxyUtilTest {


    @Test
    public void testParseProxy() {

        String html = "<tr>\n" +
                "                    <td>122.243.13.223</td>\n" +
                "                    <td>9000</td>\n" +
                "                    <td>普匿</td>\n" +
                "\t\t    <td>HTTP</td>\n" +
                "\t\t    <td>0.31 秒</td>\n" +
                "\t\t    <td>浙江省金华市 电信</td>\n" +
                "\t\t    <td>6分钟8秒前</td>\n" +
                "                </tr>";

        System.out.println("代理结果为：" + ProxyUtil.parseProxy(html));
    }

}