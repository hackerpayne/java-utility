package com.lingdonge.core.file.xml;

import com.google.common.base.Splitter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Test;

import java.util.List;

public class Dom4jUtilTest {

    @Test
    public void test() throws DocumentException {

        String xml = "<sms><result>0</result><sequence>acea26c0-3679-11e9-b7ac-b8ca3af35e29:18515490065</sequence></sms>";

        //创建解析器
        Document document = Dom4jUtil.readXml(xml);
        Element root = document.getRootElement();

        System.out.println(root.element("result").getStringValue());

        String sequence = root.element("sequence").getStringValue();
        System.out.println(sequence);

        List<String> splitResults = Splitter.on(":").trimResults().omitEmptyStrings().splitToList(sequence);
        System.out.println(splitResults.get(0));
        System.out.println(splitResults.get(1));

//        List<Element> list = root.elements();
//        for (Element e : list) {
//            //获取属性值
//            String no = e.attributeValue("sms");
//            String name = e.element("name").getText();
//            String age = e.element("age").getText();
//
//            System.out.println(no + name + age);
//        }
    }
}