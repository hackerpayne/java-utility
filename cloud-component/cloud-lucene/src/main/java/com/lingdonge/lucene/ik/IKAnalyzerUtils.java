package com.lingdonge.lucene.ik;

import com.google.common.collect.Lists;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * IK分词组件
 */
public class IKAnalyzerUtils {

    private final static Logger logger = LoggerFactory.getLogger(IKAnalyzerUtils.class);


    public static void splitter(String input) {
        //创建分词对象
        Analyzer anal = new IKAnalyzer(true);
        //分词
        TokenStream ts = anal.tokenStream("", new StringReader(input));
        CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);

        //遍历分词数据
        try {
            while (ts.incrementToken()) {
                System.out.print(term.toString() + "|");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


    /**
     * IK分词功能实现
     *
     * @return
     */
    public static List<String> spiltWords(String srcString) {
        List<String> listResults = Lists.newArrayList();
        try {
            IKSegmenter ik = new IKSegmenter(new StringReader(srcString), true);//true开启只能分词模式，如果不设置默认为false，也就是细粒度分割
            Lexeme lex;
            while ((lex = ik.next()) != null) {
                listResults.add(lex.getLexemeText());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return listResults;
    }

}
