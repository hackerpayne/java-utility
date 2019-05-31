package com.lingdonge.core.thirdparty.shorturl;

/**
 * 数据保存服务
 */
public interface StoreService {

    Boolean saveShortUrl(String longUrl, String shortUrl);

    String getDataByLongUrl(String data);

    String getDataByShortUrl(String data);
}
