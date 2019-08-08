package com.lingdonge.spider.webmagic.webmagic.downloader;

import com.lingdonge.core.http.UrlUtils;
import com.lingdonge.spider.webmagic.Request;
import com.lingdonge.spider.webmagic.Site;
import com.lingdonge.spider.webmagic.downloader.HttpClientRequestContext;
import com.lingdonge.spider.webmagic.downloader.HttpUriRequestConverter;
import org.junit.Test;

/**
 * @author code4crafter@gmail.com
 * Date: 2017/7/22
 * Time: 下午5:29
 */
public class HttpUriRequestConverterTest {

    @Test
    public void test_illegal_uri_correct() throws Exception {
        HttpUriRequestConverter httpUriRequestConverter = new HttpUriRequestConverter();
        HttpClientRequestContext requestContext = httpUriRequestConverter.convert(new Request(UrlUtils.fixIllegalCharacterInUrl("http://bj.zhongkao.com/beikao/yimo/##")), Site.me(), null);
//        assertThat(requestContext.getHttpUriRequest().getURI()).isEqualTo(new URI("http://bj.zhongkao.com/beikao/yimo/#"));
    }
}