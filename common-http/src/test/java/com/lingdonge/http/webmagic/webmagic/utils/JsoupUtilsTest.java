package com.lingdonge.http.webmagic.webmagic.utils;

import com.lingdonge.http.webmagic.utils.CrawleUtils;
import org.testng.annotations.Test;

public class JsoupUtilsTest {

    @Test
    public void testTest() throws Exception {
    }

    @Test
    public void testGetText() throws Exception {
    }

    @Test
    public void testGetText1() throws Exception {
    }

    @Test
    public void testGetText2() throws Exception {
    }

    @Test
    public void testClearTag() throws Exception {

        String html = "<a href=\"18792_2.html\" target=\"_self\"><img alt=\"歌颂母亲的古诗大全,8首经典颂扬母亲的古诗句\" src=\"http://g.meinvsj.com/uploads/allimg/170813/1-1FQ3162526.jpg\"></a></p>";


        System.out.println("clearTag结果：");
//        System.out.println(JsoupUtils.clearTag(html,new String[]{"a"}));
//        System.out.println(JsoupUtils.clearTagRemains(html,new String[]{"img","p","span"}));
        System.out.println(CrawleUtils.clearATag(html));
    }

    @Test
    public void testClearTagRemains() throws Exception {
    }

    @Test
    public void testBr2nl() throws Exception {
    }

    @Test
    public void testTestHtml() throws Exception {
    }
}