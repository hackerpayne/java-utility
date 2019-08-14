package com.lingdonge.core.util;

import org.junit.Test;

public class JudgeUtilTest {

    @Test
    public void test() {

//        String mobile = RegJudgeUtil.matchMobile("<title>【曹春泉（个体经营）联系方式】_曹春泉_13146418333-马可波罗 </title>");

//        String mobile = RegJudgeUtil.matchPhone("【台州市椒江欣居塑胶制品厂联系方式】_王吕松 &nbsp_0576-88991872-马可波罗 ");
//        System.out.println("手机号码为：" + mobile);

        // 匹配页面里面的所有号码信息
//        List<String> phones = RegJudgeUtil.getMobileList(new HttpHelper().getHtml("http://blfssy.shop.liebiao.com/"), true);
//        System.out.println(phones);
    }

    @Test
    public void isMobile() {
        System.out.println(JudgeUtil.isMobile("18515490000"));
    }

    @Test
    public void isFixedPhone() {
    }

    @Test
    public void isPostCode() {
    }

    @Test
    public void isNumber() {
    }

    @Test
    public void filterUnNumber() {
    }

    @Test
    public void isDigit() {
    }

    @Test
    public void isVehiclePlate() {
    }

    @Test
    public void isNumber12_2() {
    }

    @Test
    public void isNumber10_2() {
    }

    @Test
    public void isZipCode() {
    }

    @Test
    public void isEmail() {
    }

    @Test
    public void isIdentificationCode() {
    }

    @Test
    public void isVehicleEngineNo() {
    }
}