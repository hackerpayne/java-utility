package com.lingdonge.http.webmagic.xsoup.w3c;

import com.lingdonge.http.xsoup.w3c.DocumentAdaptor;
import org.jsoup.Jsoup;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;


/**
 * @author code4crafer@gmail.com
 */
public class DocumentAdaptorTest {

    private String html = "<html><body><div id='multithreading'>aaa<div><a href=\"https://github.com\">github.com</a></div></div></body></html>";

    @Test
    public void testDocumentAdaptor() throws Exception {
        Document document = new DocumentAdaptor(Jsoup.parse(html));
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath target = xPathfactory.newXPath();
        XPathExpression xPathExpression = target.compile("//div/a/@href");
        String result = xPathExpression.evaluate(document);
//        assertThat(result).isEqualTo("https://github.com");

    }
}
