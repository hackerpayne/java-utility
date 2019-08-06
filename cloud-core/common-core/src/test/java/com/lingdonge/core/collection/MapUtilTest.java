package com.lingdonge.core.collection;

import com.google.common.collect.Maps;
import com.lingdonge.core.http.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Map;

@Slf4j
public class MapUtilTest {

    @Test
    public void testMapToString() {

        Map<String, Object> mapPara = Maps.newHashMap();
        mapPara.put("sid", 14);//固定值
        mapPara.put("offset", 0);//偏移量
        mapPara.put("pageSize", "asdfasdfsd");//返回数量

        String value = RequestUtil.mapToQueryStr(mapPara);
        log.info(value);
    }
}
