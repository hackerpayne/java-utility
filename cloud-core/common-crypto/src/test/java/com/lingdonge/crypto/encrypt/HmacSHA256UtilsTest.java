package com.lingdonge.crypto.encrypt;


import org.junit.Test;

public class HmacSHA256UtilsTest {


    @Test
    public void encrypt() {
        String key = "234sdfdf";
        String content = "加密一下这个信息";
        String value = HmacSHA256Utils.encrypt(key, content);

        System.out.println("加密结果：");
        System.out.println(value);
    }

    @Test
    public void encrypt1() {
    }
}
