package com.lingdonge.spring.templates;

import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class FreemarkerUtilTest {

    @Test
    public void flushData() {
    }

    @Test
    public void flushData1() {
    }

    @Test
    public void flushData2() {
    }

    @Test
    public void flushStringTemplateData() throws IOException, TemplateException {

        String StringTemplate = "${v1}>${f1}";
        StringWriter out = new StringWriter();
        Map<String, Object> rootMap = new HashMap<String, Object>();
        rootMap.put("v1", 2);
        rootMap.put("f1", 3);
        rootMap.put("f3", 3);
        FreemarkerUtil.flushStringTemplateData(StringTemplate, out, rootMap);
        System.out.println(out.toString());

    }

}