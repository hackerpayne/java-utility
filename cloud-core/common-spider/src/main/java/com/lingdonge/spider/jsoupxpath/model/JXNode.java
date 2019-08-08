package com.lingdonge.spider.jsoupxpath.model;

import com.lingdonge.spider.jsoupxpath.exception.XpathSyntaxErrorException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * XPath提取后的
 * @author github.com/zhegexiaohuozi [seimimaster@gmail.com]
 * @since 2016/5/12.
 */
public class JXNode {
    private Element element;
    private boolean isText;
    private String textVal;

    public Element getElement() {
        return element;
    }

    public JXNode setElement(Element element) {
        this.element = element;
        return this;
    }

    public boolean isText() {
        return isText;
    }

    public JXNode setText(boolean text) {
        isText = text;
        return this;
    }

    public String getTextVal() {
        return textVal;
    }

    public JXNode setTextVal(String textVal) {
        this.textVal = textVal;
        return this;
    }

    public List<JXNode> sel(String xpath) throws XpathSyntaxErrorException {
        if (element ==null){
            return null;
        }
        JXDocument doc = new JXDocument(new Elements(element));
        return doc.selN(xpath);
    }

    public static JXNode e(Element element){
        JXNode n = new JXNode();
        n.setElement(element).setText(false);
        return n;
    }

    public static JXNode t(String txt){
        JXNode n = new JXNode();
        n.setTextVal(txt).setText(true);
        return n;
    }

    @Override
    public String toString() {
        if (isText){
            return textVal;
        }else {
            return element.toString();
        }
    }
}
