package com.lingdonge.core.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Emoji表情处理类
 */
@Slf4j
public class EmojiUtil {


    /**
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
        return filterEmoji(source, "");
    }

    /**
     * emoji表情替换
     *
     * @param source  原字符串
     * @param slipStr emoji表情替换成的字符串
     * @return 过滤后的字符串
     */
    public static String filterEmoji(String source, String slipStr) {
        if (StringUtils.isNotBlank(source)) {
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", slipStr);
        } else {
            return source;
        }
    }
}
