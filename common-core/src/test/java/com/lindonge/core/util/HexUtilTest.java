package com.lindonge.core.util;


import com.lindonge.core.reflect.Console;
import com.lindonge.core.encode.CharsetUtil;
import com.lindonge.core.encode.HexUtil;
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
