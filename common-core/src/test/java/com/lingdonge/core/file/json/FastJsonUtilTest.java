package com.lingdonge.core.file.json;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;

public class FastJsonUtilTest {

    @Test
    public void testBeanToMap() {

        Map<String, String> mapData = Maps.newHashMap();
        mapData.put("title", "title");
        mapData.put("body", null);

        Map map = FastJsonUtil.beanToMap(mapData, false);
        System.out.println(map);
    }

}
