package com.lingdonge.http.jsouputil;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.lindonge.core.collection.ArrayUtil;
import com.lindonge.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

/**
 * Jsoup获取数据封装
 * https://jsoup.org/
 * jsoup 是一款 Java 的HTML 解析器，可直接解析某个URL地址、HTML文本内容。它提供了一套非常省力的API，可通过DOM，CSS以及类似于JQuery的操作方法来取出和操作数据。
 */
@Slf4j
public class JsoupUtil {

    public static void main(String[] args) {
        try {
            Test();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Test() throws IOException {
//        Document doc = Jsoup.connect("http://en.wikipedia.org/").get();
//        Elements newsHeadlines = doc.select("#mp-itn b a");

//        String result = parseBodyStr("<p><b>This <i>is</i></b> <i>my sentence</i> of text.</p>");
//        System.out.println(result);

        String html = "<div class=\" g-card res-list og \" data-pcurl=\"http://tool.seowhy.com/\" data-jss=\"{&quot;urlType&quot;:&quot;web&quot;}\" data-abtest-id=\"75\" data-index=\"1\" data-page=\"1\"> <a class=\"alink\" href=\"https://m.so.com/jump?u=http%3A%2F%2Ftool.seowhy.com%2F&amp;m=0d3b3f&amp;from=m.so.com\" data-md=\"{&quot;b&quot;:&quot;title&quot;}\"> <h3 class=\"res-title\"> 鎼滃\uE63B<em>SEO宸ュ叿</em>澶у叏 SEO绔欓暱宸ュ叿搴旀湁灏芥湁 SEOWHY </h3> <p class=\"g-main summary\"> 鎼滃\uE63B<b>SEO宸ュ叿</b>澶у叏,鏀堕泦璧勬繁SEO浠庝笟鑰呭父鐢ㄧ殑鍚勭被SEO绔欓暱宸ュ叿,鍖呮嫭缁煎悎鏌ヨ\uE1D7銆佸\uE63B閾惧伐鍏枫€侀暱灏炬寲鎺樸€佹帓鍚嶅伐鍏风瓑銆� </p> </a> <div class=\"res-supplement\"> <cite><span class=\"res-site-url\">tool.seowhy.com</span></cite> </div> </div>";

        String attr = getAttr(html, "div", "data-pcurl");
        System.out.println(attr);
//        articleMap.put(article, articleMapList);
    }

    /**
     * 获取Meta里面的关键词信息
     *
     * @param doc
     * @return
     */
    public static String getMetaKeywords(Document doc) {
        if (doc == null) {
            return null;
        }
        return doc.select("meta[name=keywords]").attr("content");
    }

    /**
     * 获取Meta里面的描述信息
     *
     * @param doc
     * @return
     */
    public static String getMetaDescription(Document doc) {
        if (doc == null) {
            return null;
        }
        return doc.select("meta[name=description]").attr("content");
    }


    /**
     * 获取Text，不保留换行信息
     *
     * @param input
     * @return
     */
    public static String getText(String input) {
        return getText(input, false);
    }

    /**
     * @param input
     * @return
     */
    public static String getText(String input, boolean remainLines) {
        if (StringUtils.isEmpty(input)) {
            return "";
        }

        if (remainLines) {
            return Jsoup.clean(input, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        } else {
            return Jsoup.parse(input).text();
        }
    }

    /**
     * 通过CSSQuery删除数据
     *
     * @param input
     * @param removeCssList
     * @return
     */
    public static String removeByCss(String input, String... removeCssList) {
        try {
            Document doc = Jsoup.parse(input, "", Parser.xmlParser());
            for (String item : removeCssList) {
                if (org.apache.commons.lang3.StringUtils.isEmpty(item)) {
                    continue;
                }
                doc.select(item).remove();
            }
            return doc.toString();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    /**
     * 解析HTML并返回Body，避免生成Header进去
     *
     * @param input
     * @return
     */
    public static Document parseBody(String input) {
        Document doc = Jsoup.parseBodyFragment(input);
        doc.outputSettings().prettyPrint(false);
        return doc;
    }

    /**
     * 解析Body字符串
     *
     * @param input
     * @return
     */
    public static String parseBodyStr(String input) {
        Document doc = parseBody(input);
        if (null != doc) {
            return doc.body().html();
        }
        return null;
    }

    /**
     * 清理指定的标签
     *
     * @param input           输入的内容
     * @param listTagsToClear 要清理的标签
     * @return
     */
    public static String clearTag(String input, String... listTagsToClear) {

        String avalableTags = "a, b, blockquote, br, caption, cite, code, col, colgroup, dd, dl, dt, em, h1, h2, h3, h4, h5, h6, i, img, li, ol, p, pre, q, small, strike, strong, sub, sup, table, tbody, td, tfoot, th, thead, tr, u, ul,span,div,style";

        List<String> listAvalableTags = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(avalableTags);

        Whitelist myCustomWhitelist = new Whitelist();
        List<String> whiteLists = ArrayUtil.removeItem(listAvalableTags, Lists.newArrayList(listTagsToClear));
        myCustomWhitelist.addTags(whiteLists.toArray(new String[whiteLists.size()]));
        return Jsoup.clean(input, myCustomWhitelist);
    }

    /**
     * 清理HTML，只保留指定的标签
     *
     * @param input          输入内容
     * @param listRemainTags 要保留的标签列表
     * @return
     */
    public static String clearTagRemains(String input, String... listRemainTags) {

        Whitelist myCustomWhitelist = new Whitelist();
        myCustomWhitelist.addTags(listRemainTags);

        return Jsoup.clean(input, myCustomWhitelist);
    }

    /**
     * BR改成换行符
     *
     * @param html
     * @return
     */
    public static String br2nl(String html) {
        if (html == null) {
            return html;
        }
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\n");
        return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    /**
     * 解析HTML里面的指定属性，比如：
     * getAttr("","data-url")
     *
     * @param html
     * @param selectStr 过滤条件
     * @param attr
     * @return
     */
    public static String getAttr(String html, String selectStr, String attr) {

        Document doc = parseBody(html);

        if (null != doc) {
            return doc.select(selectStr).first().attr(attr);
        }
        return null;
    }

    /**
     * 测试HTML的处理
     *
     * @param htmlBody
     */
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
}
