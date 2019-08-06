package com.lingdonge.http.jsouputil;

import com.google.common.base.Splitter;
import com.lingdonge.core.collection.CollectionUtil;
import com.lingdonge.core.util.StringUtils;
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
        List<String> whiteLists = CollectionUtil.removeItems(listAvalableTags, listTagsToClear);
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
            return null;
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

}
