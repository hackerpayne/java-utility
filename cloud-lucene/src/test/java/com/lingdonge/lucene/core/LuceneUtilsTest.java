package com.lingdonge.lucene.core;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.lingdonge.lucene.constant.AnalyzerType;
import com.kyle.utility.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.util.List;

@Slf4j
public class LuceneUtilsTest {

    @Test
    public void test() {
        List<String> listKeywords = Lists.newArrayList();
        listKeywords.add("英雄联盟 ");
        listKeywords.add("英雄");
        listKeywords.add("hahah");
        listKeywords.add("王者联盟");
        listKeywords.add("联合国");
        listKeywords.add("连续");
        listKeywords.add("英雄如何才能联盟");
        listKeywords.add("什么是联盟，有没有英雄存在");

        LuceneUtils luceneUtils = new LuceneUtils(FileUtils.getFile(Utils.CurrentDir, "Data", "lucene").getAbsolutePath());

//        Analyzer analyzer = AnalyzerUtils.getAnalyzer(AnalyzerType.IKAnalyzer);

        luceneUtils.setAnalyzer(AnalyzerUtils.getAnalyzer(AnalyzerType.IKAnalyzer));
        luceneUtils.createKeywords(listKeywords);

        log.info("索引完成");

        List<String> listResults = luceneUtils.queryKeywords("英雄联盟吧", AnalyzerUtils.getAnalyzer(AnalyzerType.IKAnalyzer));

        System.out.println("查询结果：");
        System.out.println(Joiner.on("---").skipNulls().join(listResults));

    }

}