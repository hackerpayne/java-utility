package com.lingdonge.core.lang;


import com.lingdonge.core.algorithm.Snowflake;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;

/**
 * Snowflake单元测试
 *
 * @author Looly
 */
public class SnowflakeTest {

    @Test
    public void snowflakeTest() {
        HashSet<Long> hashSet = new HashSet<>();

        //构建Snowflake，提供终端ID和数据中心ID
        Snowflake idWorker = new Snowflake(0, 0);
        for (int i = 0; i < 1000; i++) {
            long id = idWorker.nextId();

            System.out.println("生成的ID为：" + id);
            hashSet.add(id);
        }
        Assert.assertEquals(1000L, hashSet.size());
    }
}
