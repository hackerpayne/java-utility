package com.lingdonge.core.collection;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * MapUtil扩展方法
 */
public class MapUtil extends cn.hutool.core.map.MapUtil {

    /**
     * Map排序生成签名列表字符
     *
     * @param hashParameters
     * @return
     */
    public static String getSignStr(HashMap<String, Object> hashParameters) {
        return getSignStr(hashParameters, Lists.newArrayList("sign"));
    }

    /**
     * Map排序生成签名列表字符
     * Popup弹出指定的数据，Map排序
     *
     * @param hashParameters
     * @param popKey
     * @return
     */
    public static String getSignStr(Map<String, Object> hashParameters, List<String> popKey) {

        if (isEmpty(hashParameters)) {
            return null;
        }

        Map<String, Object> newHash = hashParameters.entrySet().stream()
                .filter(item -> !popKey.contains(popKey))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (s, b) -> s));

        // 对Map进行ASCII字典排序，生成String格式，工具类由HuTool-Core提供
        String signString = join(sort(newHash), "&", "=", false);
        return signString;
    }

    /**
     * 根据条件自动过滤数据
     *
     * @param map
     * @param predicate
     * @return
     */
    public static Map<String, Object> parseMapForFilterByOptional(Map<String, Object> map, Predicate<Map.Entry> predicate) {

        return Optional.ofNullable(map).map(
                (v) -> {
                    Map params = v.entrySet().stream()
                            .filter(predicate)
                            .collect(Collectors.toMap(
                                    (e) -> (String) e.getKey(),
                                    (e) -> e.getValue()
                            ));

                    return params;
                }
        ).orElse(null);
    }


}
