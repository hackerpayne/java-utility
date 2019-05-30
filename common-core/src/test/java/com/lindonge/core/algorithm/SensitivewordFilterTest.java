package com.lindonge.core.algorithm;

import com.lindonge.core.file.FileUtil;
import com.lindonge.core.util.Utils;
import org.testng.annotations.Test;

import java.util.Set;

public class SensitivewordFilterTest {

    @Test
    public void test() {

        System.out.println(Utils.CurrentDir);
        SensitivewordFilter filter = new SensitivewordFilter(FileUtil.getFile(Utils.CurrentDir, "logs", "blackwords.txt").getAbsolutePath());
        System.out.println("敏感词的数量：" + filter.sensitiveWordMap.size());
        String string = "这是一个色情和暴力相关的文章的一些数据";

        string = "什么手机好用";

        System.out.println("待检测语句字数：" + string.length());
        long beginTime = System.currentTimeMillis();
        Set<String> set = filter.getSensitiveWord(string, 2);
        long endTime = System.currentTimeMillis();
        System.out.println("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
        System.out.println("总共消耗时间为：" + (endTime - beginTime));
    }

}