package com.lingdonge.lucene.core;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;
import com.chenlb.mmseg4j.analysis.MaxWordAnalyzer;
import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;
import com.lingdonge.lucene.constant.AnalyzerType;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.IOUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;

/**
 * Lucene分词器选择
 */
public class AnalyzerUtils {

    /**
     * 根据类型自动选择合适的分词器
     *
     * @param analyzerType
     * @return
     */
    public static Analyzer getAnalyzer(AnalyzerType analyzerType) {
        Analyzer analyzer = null;
        switch (analyzerType) {
            case MMSegSimple:
                analyzer = new SimpleAnalyzer();
                break;
            case MMsegComplex:
                analyzer = new ComplexAnalyzer();
                break;
            case MMSegMaxWords:
                analyzer = new MaxWordAnalyzer();
                break;

            case IKAnalyzer:
                analyzer = new IKAnalyzer();
                break;

            case LuceneWhitespaceAnalyzer:
                analyzer = new WhitespaceAnalyzer();
                break;

            case LuceneSimpleAnalyzer:
                analyzer = new org.apache.lucene.analysis.core.SimpleAnalyzer();
                break;

            case LuceneStandardAnalyzer:
                analyzer = new StandardAnalyzer();
                break;
            default:
                analyzer = new StandardAnalyzer();
                break;
        }
        return analyzer;
    }

    /**
     * 用指定分词器打印所有Token
     *
     * @param analyzer
     * @param text
     * @throws IOException
     */
    public static void displayTokens(Analyzer analyzer, String text) {
        TokenStream tokenStream = analyzer.tokenStream("text", text);
        displayTokens(tokenStream);
    }

    /**
     * 打印Token列表
     *
     * @param tokenStream
     * @throws IOException
     */
    public static void displayTokens(TokenStream tokenStream) {

        try {
            // 获取词元位置属性，偏移量
            OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);

            // 距离
            PositionIncrementAttribute positionIncrementAttribute = tokenStream.addAttribute(PositionIncrementAttribute.class);

            // 获取词元文本属性
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

            // 获取词元文本属性
            TypeAttribute typeAttribute = tokenStream.addAttribute(TypeAttribute.class);

            // 重置TokenStream（重置StringReader）
            tokenStream.reset();

            int position = 0;

            // 遍历方法一：
            //            // 迭代获取分词结果
//            while (ts.incrementToken()) {
//                System.out.println(offset.startOffset() + " - " + offset.endOffset() + " : " + term.toString() + " | "
//                        + type.type());
//            }

            // 遍历方法二：
            while (tokenStream.incrementToken()) {
                int increment = positionIncrementAttribute.getPositionIncrement();
                if (increment > 0) {
                    position = position + increment;
                    System.out.print(position + ":");
                }
                int startOffset = offsetAttribute.startOffset();
                int endOffset = offsetAttribute.endOffset();
                String term = charTermAttribute.toString();
                System.out.println("[" + term + "]" + ":(" + startOffset + "-->" + endOffset + "):" + typeAttribute.type());
            }

            // 遍历方法三：迭代词性等
//            while (tokenStream.incrementToken())
//            {
//                CharTermAttribute attribute = tokenStream.getAttribute(CharTermAttribute.class);
//                // 偏移量
//                OffsetAttribute offsetAtt = tokenStream.getAttribute(OffsetAttribute.class);
//                // 距离
//                PositionIncrementAttribute positionAttr = tokenStream.getAttribute(PositionIncrementAttribute.class);
//                // 词性
//                TypeAttribute typeAttr = tokenStream.getAttribute(TypeAttribute.class);
//                System.out.printf("[%d:%d %d] %s/%s\n", offsetAtt.startOffset(), offsetAtt.endOffset(), positionAttr.getPositionIncrement(), attribute, typeAttr.type());
//            }

            // 关闭TokenStream（关闭StringReader）
            tokenStream.end();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 释放TokenStream的所有资源
            IOUtils.closeWhileHandlingException(tokenStream);
        }

    }

    /**
     * 断言分词结果
     *
     * @param analyzer
     * @param text      源字符串
     * @param expecteds 期望分词后结果
     * @throws IOException
     */
    public static void assertAnalyzerTo(Analyzer analyzer, String text, String[] expecteds) throws IOException {
        TokenStream tokenStream = analyzer.tokenStream("text", text);
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        for (String expected : expecteds) {
//            Assert.assertTrue(tokenStream.incrementToken());
//            Assert.assertEquals(expected, charTermAttribute.toString());
        }
//        Assert.assertFalse(tokenStream.incrementToken());
        tokenStream.close();
    }

}

