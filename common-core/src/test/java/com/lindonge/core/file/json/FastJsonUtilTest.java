package com.lindonge.core.file.json;

import org.junit.Test;
import org.testng.collections.Maps;

import java.util.Map;

public class FastJsonUtilTest {

    @Test
    public void testBeanToMap() {

        Map<String,String> mapData= Maps.newHashMap();
        mapData.put("title","title");
        mapData.put("body",null);

        Map map = FastJsonUtil.beanToMap(mapData, false);
        System.out.println(map);
    }

}
