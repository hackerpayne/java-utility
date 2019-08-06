package com.lingdonge.core.file;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.junit.Test;

import java.util.List;

public class FileLineDupHelperTest {

    @Test
    public void test() {
        List<String> listTests = Splitter.onPattern("[\r|\n|\\||,| |\t]").omitEmptyStrings().trimResults().splitToList("wangliyan@netconcepts.cn|\r" +
                "christy@netconcepts.cn|zhengjian@netconcepts.cn,www.ok.com");

        System.out.println(Joiner.on("---").join(listTests));
    }
}