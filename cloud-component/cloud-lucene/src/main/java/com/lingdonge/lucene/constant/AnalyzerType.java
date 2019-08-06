package com.lingdonge.lucene.constant;

/**
 * 所有Lucene可以使用的分词类型
 */
public enum AnalyzerType {

    /**
     * IK分词器
     */
    IKAnalyzer,

    /**
     * MMSeg简单分词器
     */
    MMSegSimple,

    /**
     * MMSeg复杂分词器
     */
    MMsegComplex,

    /**
     * MMSeg最大分词器
     */
    MMSegMaxWords,

    /**
     * 自带的空格分词器，仅仅是去除空格，对字符没有lowcase化,不支持中文
     */
    LuceneWhitespaceAnalyzer,

    /**
     * 自带的标点分词器，功能强于WhitespaceAnalyzer, 首先会通过非字母字符来分割文本信息，然后将词汇单元统一为小写形式。该分析器会去掉数字类型的字符。
     */
    LuceneSimpleAnalyzer,//

    /**
     * 在SimpleAnalyzer的基础上增加了去除英文中的常用单词（如the，a等），也可以更加自己的需要设置常用单词；不支持中文
     */
    LuceneStopAnalyzer,

    /**
     * 默认英文分词器,英文的处理能力同于StopAnalyzer.支持中文采用的方法为单字切分。他会将词汇单元转换成小写形式，并去除停用词和标点符号。
     */
    LuceneStandardAnalyzer,

}
