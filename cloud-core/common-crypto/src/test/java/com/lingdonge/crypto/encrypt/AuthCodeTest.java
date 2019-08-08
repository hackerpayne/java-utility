package com.lingdonge.crypto.encrypt;

import org.junit.Test;

public class AuthCodeTest {

    @Test
    public void testEncrypte() {
        String test = "hello go  to bed";
        String key = "123456";
        String afStr = AuthCode.authcodeEncode(test, key);
        System.out.println("待加密字符串为：" + test);
        System.out.println("encode加密后结果为：" + afStr);

        long lStart = System.currentTimeMillis();
        System.out.println("decode解码后结果为：" + AuthCode.authcodeDecode(afStr, key));

        long lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("加解密耗时：" + lUseTime + "毫秒");
        String deStr = AuthCode
                .authcodeDecode(
                        "0084tuF6jOu8bVvO//fcV6fXL/CCcUYVJby2nQOofjRasbvrqYNupR6eQJ2rDnhh1XvxWTft4Ub5TSdZA2Y3Ts0yhH8UrziYy5dXl3MHC5freHTOdAfgfFofcnQvLwo+BvD1hT7J9qw57Ral4NC+KNTc/Vj1CzPpftA5P6qUO3KB",
                        key);
        System.out.println("--------decode:" + deStr);
    }
}