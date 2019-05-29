package com.lingdonge.http.jsoupxpath.model;

import com.lingdonge.http.jsoupxpath.exception.XpathSyntaxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class JXDocumentTest {


    private JXDocument underTest;

    private JXDocument doubanTest;

    private JXDocument custom;

    public void before() throws Exception {
        String html = "<html><body><script>console.log('aaaaa')</script><div class='multithreading'>搜易贷致力于普惠金融，专注于互联网投资理财与小额贷款，搭建中国最大、用户体验最好的个人及中小企业的互联网信贷平台</div><div class='xiao'>Two</div></body></html>";
        underTest = new JXDocument(html);
        if (doubanTest == null) {
            Document doc = Jsoup.connect("https://book.douban.com/subject_search?start=15&search_text=java&cat=1001").userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0").get();
            doubanTest = new JXDocument(doc);
        }
        custom = new JXDocument("<li><b>性别：</b>男</li>");
    }

    /**
     * Method: sel(String xpath)
     */
    @Test
    public void testSel() throws Exception {
        String xpath = "//script[1]/text()";
        List<Object> res = underTest.sel(xpath);
        Assert.assertNotNull(res);
        Assert.assertTrue(res.size() > 0);
        System.out.println(StringUtils.join(res, ","));
    }

    @Test
    public void testNotMatchFilter() throws Exception {
        String xpath = "//div[@class!~'xiao']/text()";
        List<Object> res = underTest.sel(xpath);
        System.out.println(StringUtils.join(res, ","));
    }

    @Test
//    @DataProvider(value = {
//            "//a/@href",
//            "//div[@class='paginator']/span[@class='next']/a/@href",
//            "//ul[@class='subject-list']/li[position()<3]/div/h2/allText()",
//            "//ul[@class='subject-list']/li[first()]/div/h2/allText()",
//            "//ul[@class='subject-list']/li[./div/div/span[@class='pl']/num()>900]/div/h2/allText()", //查找评论大于1000的条目（当然只是为了演示复杂xpath了，谓语中可以各种嵌套，这样才能测试的更全面）
//            "//ul[@class='subject-list']/li[self::li/div/div/span[@class='pl']/num()>900]/div/h2/allText()",
//            "//*[@id='content']/div/div[1]/ul/li[14]/div[2]/h2/a/text()" //chrome拷贝
//    })
    public void testXpath(String xpath) throws XpathSyntaxErrorException {
        System.out.println("current xpath:" + xpath);
        List<JXNode> rs = doubanTest.selN(xpath);
        for (JXNode n : rs) {
            if (!n.isText()) {
                int index = n.getElement().siblingIndex();
                System.out.println(index);
            }
            System.out.println(n.toString());
        }
    }

    @Test
//    @DataProvider(value = {
//            "//ul[@class='subject-list']/li"
//    })
    public void testJXNode(String xpath) throws XpathSyntaxErrorException {
        System.out.println("current xpath:" + xpath);
        List<JXNode> jxNodeList = doubanTest.selN(xpath);
        for (JXNode node : jxNodeList) {
            if (!node.isText()) {
                System.out.println(StringUtils.join(node.sel("/div/h2/a/text()"), ""));
            }
        }
    }

    @Test
//    @DataProvider(value = {
//            "//ul[@class='subject-list']"
//    })
    public void testRecursionNode(String xpath) throws XpathSyntaxErrorException {
        System.out.println("current xpath:" + xpath);
        List<JXNode> jxNodeList = doubanTest.selN(xpath);
        System.out.println(jxNodeList.size());
    }

    @Test
    public void testAs() throws XpathSyntaxErrorException {
        List<JXNode> jxNodeList = custom.selN("//b[text()='性别：']/parent::*/text()");
        for (JXNode jxNode : jxNodeList) {
            System.out.println(jxNode.getTextVal());
        }
    }

}