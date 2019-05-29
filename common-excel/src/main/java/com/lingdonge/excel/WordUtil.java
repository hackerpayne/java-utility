package com.lingdonge.excel;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * 使用Filemagic识别文件编码，POI 3.17新增加的一个特性
 * https://poi.apache.org/apidocs/org/apache/poi/poifs/filesystem/FileMagic.html
 */
public class WordUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExcelHelper.class);


    /**
     * 自动识别编码读取Word文档
     * https://segmentfault.com/a/1190000012165530
     * @param filePath
     * @param is
     * @return
     */
    public static String readDoc(String filePath, InputStream is) {
        String text = "";
        is = FileMagic.prepareToCheckMagic(is);
        try {
            if (FileMagic.valueOf(is) == FileMagic.OLE2) {
                WordExtractor ex = new WordExtractor(is);
                text = ex.getText();
                ex.close();
            } else if (FileMagic.valueOf(is) == FileMagic.OOXML) {
                XWPFDocument doc = new XWPFDocument(is);
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                text = extractor.getText();
                extractor.close();
            }
        } catch (Exception e) {
            logger.error("for file " + filePath, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return text;
    }

}
