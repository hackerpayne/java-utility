package com.lingdonge.core.reflect;

import org.junit.Test;

public class NamingUtilTest {


    @Test
    public void test() {
        System.out.println(NamingUtil.camelToUnderline("a.userName"));
        System.out.println(NamingUtil.underlineToCamel("a_user_Name"));
    }

    @Test
    public void getFirstUpperName() {
    }

    @Test
    public void getFirstLowerName() {
    }

    @Test
    public void camelToUnderlineNew() {
    }

    @Test
    public void camelToUnderline() {
    }

    @Test
    public void underlineToCamelNew() {
        System.out.println(NamingUtil.camelToUnderlineNew("a.userName"));
    }

    @Test
    public void underlineToCamel() {
        System.out.println(NamingUtil.underlineToCamelNew("a_user_Name_or_Invalid"));
    }
}