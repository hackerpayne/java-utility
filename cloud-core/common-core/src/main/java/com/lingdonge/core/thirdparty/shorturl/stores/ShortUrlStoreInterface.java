package com.lingdonge.core.thirdparty.shorturl.stores;

/**
 * 数据保存服务
 */
public interface ShortUrlStoreInterface {

    /**
     * 保存长网址对应的短网址
     * @param longUrl
     * @param shortUrl
     * @return
     */
    Boolean saveShortUrl(String longUrl, String shortUrl);

    /**
     * 根据长网址查短网址
     * @param longUrl
     * @return
     */
    String getByLongUrl(String longUrl);

    /**
     * 根据短网址查长网址
     * @param shortUrl
     * @return
     */
    String getByShortUrl(String shortUrl);
}
