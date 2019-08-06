//package com.kyle.lucene;
//
//import com.huaban.analysis.jieba.JiebaSegmenter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * 结巴分词组件
// */
//public class JiebaAnalysisUtils {
//
//    private final static Logger logger = LoggerFactory.getLogger(JiebaAnalysisUtils.class);
//
//    public void testDemo() {
//        JiebaSegmenter segmenter = new JiebaSegmenter();
//        String[] sentences =
//                new String[] {"这是一个伸手不见五指的黑夜。我叫孙悟空，我爱北京，我爱Python和C++。", "我不喜欢日本和服。", "雷猴回归人间。",
//                        "工信处女干事每月经过下属科室都要亲口交代24口交换机等技术性器件的安装工作", "结果婚的和尚未结过婚的"};
//        for (String sentence : sentences) {
//            System.out.println(segmenter.process(sentence, JiebaSegmenter.SegMode.INDEX).toString());
//        }
//    }
//
//}
