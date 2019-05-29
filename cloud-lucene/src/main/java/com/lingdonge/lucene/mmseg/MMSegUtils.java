package com.lingdonge.lucene.mmseg;

import com.chenlb.mmseg4j.*;
import com.lingdonge.lucene.constant.AnalyzerType;
import com.lingdonge.lucene.core.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * mmseg4j两种分词方法： Complex、Simple
 * 三种分词模式：Complex、Simple、MaxWord，默认是MaxWord。
 * mmseg4j用Chih-HaoTsai的MMSeg算法实现的中文分词器，MMSeg算法有两种分词方法：Simple和Complex，都是基于正向最大匹配。
 *
 *
 data/chars.dic 是单字与语料中的频率，一般不用改动，1.5版本中已经加到mmseg4j的jar里了，我们不需要关心它，当然你在词库目录放这个文件可能覆盖它。
 data/units.dic 是单字的单位，默认读jar包里的，你也可以自定义覆盖它。
 data/words.dic 是词库文件，一行一词，当然你也可以使用自己的，1.5版本使用 sogou 词库，1.0的版本是用 rmmseg 带的词库。
 data/wordsxxx.dic 1.6版支持多个词库文件，data 目录（或你定义的目录）下读到"words"前缀且".dic"为后缀的文件。如：data/words-my.dic。


 */
public class MMSegUtils {
    private final static Logger logger = LoggerFactory.getLogger(MMSegUtils.class);


    public static void main(String[] args) throws IOException {

        Analyzer analyzer = AnalyzerUtils.getAnalyzer(AnalyzerType.MMsegComplex);

        AnalyzerUtils.displayTokens(analyzer,"这是一个中文分词的例子，你可以直接运行它！IKAnalyer can analysis english text too");
    }

    /**
     * 指定自定义词典
     */
    public void customDic() throws IOException {
        String txt = "在一起并发生了中文分词.";
        // user-defined dictionary parent-path
        Dictionary dic = Dictionary.getInstance("src\\resources\\dict");
        Seg seg = new ComplexSeg(dic);
        MMSeg mmSeg = new MMSeg(new StringReader(txt), seg);
        Word word = null;
        while ((word = mmSeg.next()) != null) {
            System.out.print(word + "|");
        }
    }
}
