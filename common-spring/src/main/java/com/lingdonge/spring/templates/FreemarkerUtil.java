package com.lingdonge.spring.templates;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Freemarker工具类
 * 依赖于org.apache.commons.lang3、org.apache.commons.io
 *
 * @author Sinamber
 * usage：
 * String templateFile = "src/test/resources/template/ftl/foo.ftl";
 * String outFile = "src/test/resources/output/foo.html";
 * Map<String, Object> rootMap = new HashMap<String, Object>();
 * <p>
 * rootMap.put("id", 3306);
 * rootMap.put("name", "Sinamber");
 * <p>
 * Writer out = new FileWriter(new File(outFile));
 * FreemarkerUtil.flushData(templateFile, out, rootMap);
 * <p>
 * *************************************************************************
 * foo.ftl --> Hello ${name} , your Id is ${id}
 * foo.html will out put --> Hello Sinamber , your Id is 3306
 */
@Slf4j
public class FreemarkerUtil {

    private static Configuration configuration = null;

    private static Configuration getConfiguration() {
        if (configuration == null) {
            configuration = new Configuration(Configuration.VERSION_2_3_0);
        }
        return configuration;
    }

    /**
     * 根据模板渲染数据
     *
     * @param templateFile freemarker模板文件路径
     * @param out          目标对象
     * @param rootMap      值栈对象
     * @throws IOException
     * @throws TemplateException
     */
    public static void flushData(String templateFile, Writer out, Object rootMap) throws IOException, TemplateException {
        flushData(templateFile, out, rootMap, "UTF-8", "#");
    }

    /**
     * 根据模板渲染数据
     *
     * @param templateFile freemarker模板文件路径
     * @param rootMap      值栈对象
     * @throws IOException
     * @throws TemplateException
     */
    public static String flushData(String templateFile, Object rootMap) throws IOException, TemplateException {
        StringWriter out = new StringWriter();
        flushData(templateFile, out, rootMap, "UTF-8", "#");
        return out.toString();
    }

    /**
     * 根据模板渲染数据
     *
     * @param templateFile freemarker模板文件路径
     * @param out          目标对象
     * @param rootMap      值栈对象
     * @param encoding     编码，默认UTF-8
     * @param numberFormat 数字格式化，默认#000.00
     * @throws IOException
     * @throws TemplateException
     */
    public static void flushData(String templateFile, Writer out, Object rootMap, String encoding, String numberFormat)
            throws IOException, TemplateException {
        File template = new File(templateFile);
        if (!template.exists()) {
            throw new IOException("templateFile is NOT exist");
        }
        if (!template.isFile()) {
            throw new IOException("templateFile is NOT a File");
        }

        Configuration cfg = getConfiguration();
        cfg.setDefaultEncoding(StringUtils.defaultIfEmpty(encoding, "UTF-8"));
        cfg.setNumberFormat(StringUtils.defaultIfEmpty(numberFormat, "#"));

        String fullPath = FilenameUtils.getFullPathNoEndSeparator(templateFile);
        String templateName = FilenameUtils.getName(templateFile);
        File templateDir = new File(fullPath);
        cfg.setDirectoryForTemplateLoading(templateDir);
        Template t = cfg.getTemplate(templateName);
        t.process(rootMap, out);
    }

    /**
     * 根据字符串模板渲染数据
     *
     * @param StringTemplate
     * @param out
     * @param rootMap
     * @throws IOException
     * @throws TemplateException
     */
    public static void flushStringTemplateData(String StringTemplate, Writer out, Object rootMap) throws IOException,
            TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_0);
        String templateName = "_innerTemplate";
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate(templateName, StringTemplate);
        cfg.setTemplateLoader(stringLoader);
        Template temp = cfg.getTemplate(templateName, "utf-8");
        temp.process(rootMap, out);
        out.flush();
    }

    /**
     * 根据字符串模板渲染数据
     *
     * @param StringTemplate
     * @param rootMap
     * @throws IOException
     * @throws TemplateException
     */
    public static String flushStringTemplateData(String StringTemplate, Object rootMap) throws IOException,
            TemplateException {
        StringWriter out = new StringWriter();
        flushStringTemplateData(StringTemplate, out, rootMap);
        return out.toString();
    }


}
