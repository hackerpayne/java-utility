package com.lingdonge.core.util;

import cn.hutool.core.collection.CollUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class RandomUtilTest {
    @Test
    public void randomEleSetTest() {
        Set<Integer> set = RandomUtil.randomEleSet(CollUtil.newArrayList(1, 2, 3, 4, 5, 6), 2);
        Assert.assertEquals(set.size(), 2);
    }
}
