package com.lingdonge.spider.webmagic.webmagic.utils;


import com.lingdonge.core.http.UrlUtils;
import org.junit.Test;

/**
 * @author code4crafter@gmail.com <br>
 * Date: 13-4-21
 * Time: 下午2:22
 */
public class UrlUtilsTest {

    @Test
    public void testFixRelativeUrl() {
        String absoluteUrl = UrlUtils.canonicalizeUrl("aa", "http://www.dianping.com/sh/ss/com");
//        assertThat(absoluteUrl).isEqualTo("http://www.dianping.com/sh/ss/aa");

        absoluteUrl = UrlUtils.canonicalizeUrl("../aa", "http://www.dianping.com/sh/ss/com");
//        assertThat(absoluteUrl).isEqualTo("http://www.dianping.com/sh/aa");

        absoluteUrl = UrlUtils.canonicalizeUrl("../mshz", "http://www.court.gov.cn/zgcpwsw/zgrmfy/");
//        assertThat(absoluteUrl).isEqualTo("http://www.court.gov.cn/zgcpwsw/mshz");

        absoluteUrl = UrlUtils.canonicalizeUrl("..aa", "http://www.dianping.com/sh/ss/com");
//        assertThat(absoluteUrl).isEqualTo("http://www.dianping.com/sh/ss/..aa");

        absoluteUrl = UrlUtils.canonicalizeUrl("../../aa", "http://www.dianping.com/sh/ss/com/");
//        assertThat(absoluteUrl).isEqualTo("http://www.dianping.com/sh/aa");

        absoluteUrl = UrlUtils.canonicalizeUrl("../../aa", "http://www.dianping.com/sh/ss/com");
//        assertThat(absoluteUrl).isEqualTo("http://www.dianping.com/aa");
    }

    @Test
    public void testGetDomain() {
        String url = "http://www.dianping.com/aa/";
//        Assert.assertEquals("www.dianping.com",UrlUtils.getDomain(url));
        url = "www.dianping.com/aa/";
//        Assert.assertEquals("www.dianping.com",UrlUtils.getDomain(url));
        url = "http://www.dianping.com";
//        Assert.assertEquals("www.dianping.com",UrlUtils.getDomain(url));
    }

    @Test
    public void testGetHost() {
        String url = "http://www.dianping.com/aa/";
        System.out.println(UrlUtils.getHost(url));

        url = "www.dianping.com/aa/";
        System.out.println(UrlUtils.getHost(url));

        url = "http://www.dianping.com";
        System.out.println(UrlUtils.getHost(url));
    }

    @Test
    public void testEncode() {
        String url = "https://www.baidu.com/baidu?wd=java+url+encode+%E4%B8%AD%E6%96%87&tn=monline_4_dg&ie=utf-8";
        System.out.println("URL编码结果为：");
        System.out.println(UrlUtils.decode(url, "utf-8"));
    }


}
