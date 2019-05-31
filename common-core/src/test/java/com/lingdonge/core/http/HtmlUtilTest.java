package com.lingdonge.core.http;

import org.testng.annotations.Test;

public class HtmlUtilTest {

    @Test
    public void testMetaFresh() {
        String html = "<meta http-equiv=\"refresh\" content=\"0; url='http://bbs.moonseo.cn/'\" />";
        System.out.println(HtmlUtil.getMetaRefresh(html));
    }
}