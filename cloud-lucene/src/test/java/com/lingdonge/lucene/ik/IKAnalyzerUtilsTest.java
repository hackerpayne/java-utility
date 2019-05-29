package com.lingdonge.lucene.ik;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;

@Slf4j
public class IKAnalyzerUtilsTest {

    @Test
    public void main() {

        String str = "文嘉(1501-1583)";
        List<String> listSplits = IKAnalyzerUtils.spiltWords(str);

        System.out.println("IK分词结果为：");
        String spliResults = Joiner.on("/").skipNulls().join(listSplits);
        System.out.println(spliResults);
    }

}
