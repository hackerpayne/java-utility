package com.lingdonge.core.file.xml;

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

}
