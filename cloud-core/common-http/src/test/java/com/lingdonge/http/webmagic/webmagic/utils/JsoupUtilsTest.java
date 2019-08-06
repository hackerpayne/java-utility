package com.lingdonge.http.webmagic.webmagic.utils;

import com.lingdonge.http.jsouputil.JsoupUtil;
import com.lingdonge.http.webmagic.utils.CrawleUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

public class JsoupUtilsTest {

    /**
     * 测试HTML的处理
     *
     * @param htmlBody
     */
    @Test
    public void TestHtml(String htmlBody) {
        Document doc = Jsoup.parse(htmlBody);
        String title = doc.title().toString();//取标题

        Elements pngs = doc.select("img[src$=.png]");
        //扩展名为.png的图片

        Element masthead = doc.select("div.masthead").first();
        //class等于masthead的div标签

        Elements resultLinks = doc.select("h3.r > a"); //在h3元素之后的a元素

        //获取所有链接
        Elements listLinks = doc.select("a[href]");
        System.out.format("\nLinks: (%d)", listLinks.size());
        for (Element link : listLinks) {
            System.out.format(" * a: <%s>  (%s)", link.attr("abs:href"), link.text().trim(), 35);
        }

        //获取class的元素
        Elements ListDiv = doc.getElementsByAttributeValue("class", "postTitle");
        for (Element element : ListDiv) {
            Elements links = element.getElementsByTag("a");
            for (Element link : links) {
                String linkHref = link.attr("href");
                String absHref = link.attr("abs:href"); // "http://www.open-open.com/"。获取绝对地址
                String linkText = link.text().trim();
                System.out.println(linkHref);
                System.out.println(linkText);
            }
        }
    }

    @Test
    public void Test() throws IOException {
//        Document doc = Jsoup.connect("http://en.wikipedia.org/").get();
//        Elements newsHeadlines = doc.select("#mp-itn b a");

//        String result = parseBodyStr("<p><b>This <i>is</i></b> <i>my sentence</i> of text.</p>");
//        System.out.println(result);

        String html = "<div class=\" g-card res-list og \" data-pcurl=\"http://tool.seowhy.com/\" data-jss=\"{&quot;urlType&quot;:&quot;web&quot;}\" data-abtest-id=\"75\" data-index=\"1\" data-page=\"1\"> <a class=\"alink\" href=\"https://m.so.com/jump?u=http%3A%2F%2Ftool.seowhy.com%2F&amp;m=0d3b3f&amp;from=m.so.com\" data-md=\"{&quot;b&quot;:&quot;title&quot;}\"> <h3 class=\"res-title\"> 鎼滃\uE63B<em>SEO宸ュ叿</em>澶у叏 SEO绔欓暱宸ュ叿搴旀湁灏芥湁 SEOWHY </h3> <p class=\"g-main summary\"> 鎼滃\uE63B<b>SEO宸ュ叿</b>澶у叏,鏀堕泦璧勬繁SEO浠庝笟鑰呭父鐢ㄧ殑鍚勭被SEO绔欓暱宸ュ叿,鍖呮嫭缁煎悎鏌ヨ\uE1D7銆佸\uE63B閾惧伐鍏枫€侀暱灏炬寲鎺樸€佹帓鍚嶅伐鍏风瓑銆� </p> </a> <div class=\"res-supplement\"> <cite><span class=\"res-site-url\">tool.seowhy.com</span></cite> </div> </div>";

        String attr = JsoupUtil.getAttr(html, "div", "data-pcurl");
        System.out.println(attr);
//        articleMap.put(article, articleMapList);
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