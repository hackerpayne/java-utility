package org.wltea.analyzer.sample;

import com.lingdonge.lucene.core.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;

/**
 * 使用IKAnalyzer进行分词的演示 2012-10-22
 */
public class IKAnalzyerDemo {

    public static void main(String[] args) throws IOException {
        // 构建IK分词器，使用smart分词模式
        Analyzer analyzer = new IKAnalyzer(true);

        AnalyzerUtils.displayTokens(analyzer,"这是一个中文分词的例子，梦幻诛仙2非常好看好玩，你可以直接运行它！IKAnalyer can analysis english text too");

    }

}
