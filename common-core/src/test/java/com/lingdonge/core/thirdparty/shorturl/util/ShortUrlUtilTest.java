package com.lingdonge.core.thirdparty.shorturl.util;

import org.junit.Test;

import java.util.stream.IntStream;

public class ShortUrlUtilTest {


    @Test
    public void tesst() {
        String sLongUrl = "http://www.baidu.com"; // 3BD768E58042156E54626860E241E999

        IntStream.range(1, 10).forEach(i -> {
            String shortUrl = ShortUrlUtil.shortenBy62Radix(i + 10000000);
            System.out.println("算法一：[" + i + "] is " + shortUrl);
            System.out.println("算法二：[" + i + "] is " + ShortUrlUtil.shortenByMd5(sLongUrl, 5));
            System.out.println("----------------------");
        });


    }

}