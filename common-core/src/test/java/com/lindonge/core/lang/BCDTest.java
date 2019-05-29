package com.lindonge.core.lang;


import com.lindonge.core.encode.BCD;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BCDTest {

    @Test
    public void bcdTest() {
        String strForTest = "123456ABCDEF";

        //转BCD
        byte[] bcd = BCD.strToBcd(strForTest);
        String str = BCD.bcdToStr(bcd);
        //解码BCD
        Assert.assertEquals(strForTest, str);
    }
}
