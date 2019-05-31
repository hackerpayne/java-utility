package com.lingdonge.core.util;


import com.lingdonge.core.reflect.Console;
import com.lingdonge.core.encode.CharsetUtil;
import com.lingdonge.core.encode.HexUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * HexUtil单元测试
 *
 * @author Looly
 */
public class HexUtilTest {

    @Test
    public void hexStrTest() {
        String str = "我是一个字符串";

        String hex = HexUtil.encodeHexStr(str, CharsetUtil.CHARSET_UTF_8);
        Console.log(hex);

        String decodedStr = HexUtil.decodeHexStr(hex);

        Assert.assertEquals(str, decodedStr);
    }
}
