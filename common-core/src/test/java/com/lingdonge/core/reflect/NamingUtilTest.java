package com.lingdonge.core.reflect;

import org.junit.Test;

public class NamingUtilTest {


    @Test
    public void test() {
        System.out.println(NamingUtil.camelToUnderline("a.userName"));
        System.out.println(NamingUtil.underlineToCamel("a_user_Name"));
    }

}