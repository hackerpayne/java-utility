package com.lingdonge.core.encode;

import org.junit.Test;

public class EncodeUtilTest {

    @Test
    public void test() {
        String unicode = EncodeUtil.str2Unicode("中文");

        System.out.println(unicode);

        String decode = EncodeUtil.unicode2Str(unicode);

        System.out.println("还原结果：" + decode);
    }

}