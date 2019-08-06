package com.lingdonge.http.faker;

import org.junit.Test;

public class MobileHelperTest {
    @Test
    public void testIsMobile() throws Exception {
    }

    @Test
    public void testIsValidMobile() throws Exception {

        System.out.println("判断三网电话号码");
        System.out.println(MobileHelper.isValidMobile("13320932696"));
        System.out.println(MobileHelper.isValidMobile("18280249049"));
        System.out.println(MobileHelper.isValidMobile("18515490065"));
        System.out.println(MobileHelper.isValidMobile("15146577554"));
    }

    @Test
    public void testGetMobileList() throws Exception {
    }

    @Test
    public void testMatchMobile() throws Exception {
    }

    @Test
    public void testMatchPhone() throws Exception {
    }

    @Test
    public void testClearMobile() throws Exception {
    }

    @Test
    public void testGetMobileType1() throws Exception {
    }

    @Test
    public void testGetMobileType() throws Exception {

        System.out.println("111111");
        System.out.println(MobileHelper.MobileEnum.NONE.ordinal());
        System.out.println(MobileHelper.MobileEnum.CHINA_MOBILE.ordinal());
        System.out.println(MobileHelper.MobileEnum.UNICOM.ordinal());
        System.out.println(MobileHelper.MobileEnum.TELECOM.ordinal());
        System.out.println(MobileHelper.MobileEnum.valueOf(0));
        System.out.println(MobileHelper.MobileEnum.valueOf(1));
        System.out.println(MobileHelper.MobileEnum.valueOf(2));
        System.out.println(MobileHelper.MobileEnum.valueOf(3));

        System.out.println("匹配结果");
        System.out.println(MobileHelper.getMobileType("18515490065"));
        System.out.println(MobileHelper.getMobileType("18280249049"));
        System.out.println(MobileHelper.getMobileType("13320932696"));
        System.out.println(MobileHelper.getMobileType("18990301978"));
        System.out.println(MobileHelper.getMobileType("12515490065"));
        System.out.println(MobileHelper.getMobileType("13515490065"));
        System.out.println(MobileHelper.getMobileType("17515490065"));
        System.out.println(MobileHelper.getMobileType("13315490065"));
    }

}
