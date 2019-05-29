package com.lindonge.core.lang;


import com.lindonge.core.util.StringUtils;
import com.lindonge.core.encode.Base64Util;
import org.testng.Assert;
import org.testng.annotations.Test;

public class Base64UtilTest {

    @Test
    public void encodeAndDecodeTest() {
        String a = "伦家是一个非常长的字符串66";
        String encode = Base64Util.encode(a);
        Assert.assertEquals("5Lym5a625piv5LiA5Liq6Z2e5bi46ZW/55qE5a2X56ym5LiyNjY=", encode);

        String decodeStr = Base64Util.decode(encode);
        Assert.assertEquals(a, decodeStr);
    }

    @Test
    public void urlSafeEncodeAndDecodeTest() {
        String a = "伦家需要安全感55";
        String encode = StringUtils.utf8Str(Base64Util.encode(a));
        Assert.assertEquals("5Lym5a626ZyA6KaB5a6J5YWo5oSfNTU", encode);

        String decodeStr = Base64Util.decode(encode);
        Assert.assertEquals(a, decodeStr);
    }
}
