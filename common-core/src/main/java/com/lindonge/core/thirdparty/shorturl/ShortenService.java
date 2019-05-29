package com.lindonge.core.thirdparty.shorturl;

/**
 * 缩短服务
 */
public interface ShortenService {

    /**
     * 长网址缩短服务
     *
     * @param longUrl
     * @return
     */
    String shorten(String longUrl);

    /**
     * 指定长度缩短网址
     *
     * @param longUrl
     * @param shortLength
     * @return
     */
    String shorten(String longUrl, Integer shortLength);

}
