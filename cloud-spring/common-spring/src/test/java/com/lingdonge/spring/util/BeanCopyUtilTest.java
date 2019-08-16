package com.lingdonge.spring.util;

import com.lingdonge.spring.bean.token.JwtUserInfo;
import org.junit.Test;
import org.springframework.cglib.beans.BeanCopier;

import static org.junit.Assert.*;

public class BeanCopyUtilTest {

    @Test
    public void copy() {
        BeanCopier beanCopier = BeanCopier.create(JwtUserInfo.class, JwtUserInfo.class, false);
//        beanCopier.copy(source, target);
    }

    @Test
    public void mapToList() {
    }

    @Test
    public void mapToEntity() {
    }
}