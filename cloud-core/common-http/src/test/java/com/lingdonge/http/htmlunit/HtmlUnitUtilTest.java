package com.lingdonge.http.htmlunit;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.junit.Test;

@Slf4j
public class HtmlUnitUtilTest {

    @Test
    public void getHtmlPageResponse() {
    }

    @Test
    public void getHtmlPageResponseAsDocument() throws Exception {
        Document doc = HtmlUnitUtil.getInstance().getHtmlPageResponseAsDocument("http://www.baidu.com");
        log.info("Html结果为：" + doc.html());
    }
}