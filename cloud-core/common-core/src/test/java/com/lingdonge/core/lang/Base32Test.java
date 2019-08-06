package com.lingdonge.core.lang;

import com.lingdonge.core.encode.Base32;
import org.junit.Assert;
import org.junit.Test;

public class Base32Test {

    @Test
    public void encodeAndDecodeTest() {
        String a = "伦家是一个非常长的字符串";
        String encode = Base32.encode(a);
        Assert.assertEquals("4S6KNZNOW3TJRL7EXCAOJOFK5GOZ5ZNYXDUZLP7HTKCOLLMX46WKNZFYWI", encode);

        String decodeStr = Base32.decodeStr(encode);
        Assert.assertEquals(a, decodeStr);
    }

    @Test
    public void Test() {
        String result = Base32.encode("hahahahaha");
        System.out.println("结果1为：" + result);

        result = Base32.decodeStr(result);
        System.out.println("解密结果为：" + result);
    }

}
