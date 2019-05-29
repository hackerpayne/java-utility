package com.lindonge.core.file.xml;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 使用Dom4j解析XML
 * https://dom4j.github.io/
 */
@Slf4j
public class Dom4jUtil {


    /**
     * 从字符串中读取XML到Document文档对象
     *
     * @param xml
     * @return
     */
    public static Document readXml(String xml) {
        Document xmlDoc = null;
        try {
//            xmlDoc = sax.read(xml); // 有时会发现no protocol异常，原因是编码问题，解决办法如下：
            xmlDoc = new SAXReader().read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        } catch (DocumentException | UnsupportedEncodingException e) {
            log.error(e.getMessage());
        }
        return xmlDoc;
    }

    public static void main(String[] args) throws DocumentException {

        String xml = "<sms><result>0</result><sequence>acea26c0-3679-11e9-b7ac-b8ca3af35e29:18515490065</sequence></sms>";

        //创建解析器
        Document document = readXml(xml);
        Element root = document.getRootElement();

        System.out.println(root.element("result").getStringValue());

        String sequence = root.element("sequence").getStringValue();
        System.out.println(sequence);

        List<String> splitResults= Splitter.on(":").trimResults().omitEmptyStrings().splitToList(sequence);
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
