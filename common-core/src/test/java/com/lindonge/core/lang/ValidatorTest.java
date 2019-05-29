package com.lindonge.core.lang;

import com.lindonge.core.util.ValidateUtil;
import com.lindonge.core.exceptions.ValidateException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 验证器单元测试
 *
 * @author Looly
 */
public class ValidatorTest {
//    @Test
//    public void isBirthdayTest() {
//        boolean b = ValidateUtil.isBirthday("20150101");
//        Assert.assertTrue(b);
//        boolean b2 = ValidateUtil.isBirthday("2015-01-01");
//        Assert.assertTrue(b2);
//        boolean b3 = ValidateUtil.isBirthday("2015.01.01");
//        Assert.assertTrue(b3);
//        boolean b4 = ValidateUtil.isBirthday("2015年01月01日");
//        Assert.assertTrue(b4);
//        boolean b5 = ValidateUtil.isBirthday("2015.01.01");
//        Assert.assertTrue(b5);
//
//        //验证年非法
//        Assert.assertFalse(ValidateUtil.isBirthday("2095.05.01"));
//        //验证月非法
//        Assert.assertFalse(ValidateUtil.isBirthday("2015.13.01"));
//        //验证日非法
//        Assert.assertFalse(ValidateUtil.isBirthday("2015.02.29"));
//    }

    @Test
    public void isCitizenIdTest() {
        boolean b = ValidateUtil.isCitizenId("150218199012123389");
        Assert.assertTrue(b);
    }

    //	@Test(expected=ValidateException.class)
    public void validateTest() throws ValidateException {
        ValidateUtil.validateChinese("我是一段zhongwen", "内容中包含非中文");
    }
}
