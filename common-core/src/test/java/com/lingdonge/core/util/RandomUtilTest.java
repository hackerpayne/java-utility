package com.lingdonge.core.util;

import com.lingdonge.core.collection.CollectionUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;

public class RandomUtilTest {
    @Test
    public void randomEleSetTest() {
        Set<Integer> set = RandomUtil.randomEleSet(CollectionUtil.newArrayList(1, 2, 3, 4, 5, 6), 2);
        Assert.assertEquals(set.size(), 2);
    }
}
