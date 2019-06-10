package com.lingdonge.spring.validation;

import com.lingdonge.spring.bean.token.JwtUserInfo;
import org.junit.Test;

public class ValidationUtilTest {

    @Test
    public void jwtTest() {

        JwtUserInfo userInfo = new JwtUserInfo();
        userInfo.setUserId("sfadf");
        userInfo.setSubject("subject");

        ValidResult validResult = ValidationUtil.validateBean(userInfo);
        if (validResult.hasErrors()) {
            String errors = validResult.getErrors();
            System.out.println(errors);
        }
    }

}