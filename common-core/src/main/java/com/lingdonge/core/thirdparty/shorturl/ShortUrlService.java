package com.lingdonge.core.thirdparty.shorturl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ShortUrlService {

    private StoreService storeService;

    private ShortenService shortenService;

    public ShortUrlService(StoreService storeService, ShortenService shortenService) {
        this.storeService = storeService;
        this.shortenService = shortenService;
    }

    /**
     * 长网址转短网址
     *
     * @param longUrl
     * @return
     */
    public String convertShort(String longUrl) {
        String shortUrl = this.storeService.getDataByLongUrl(longUrl);
        if (StringUtils.isEmpty(shortUrl)) {
            shortUrl = this.shortenService.shorten(longUrl);
            if (StringUtils.isEmpty(shortUrl)) {
                log.error("Cannot convert long url to short url");
            } else {
                this.storeService.saveShortUrl(longUrl, shortUrl);
            }
        }
        return shortUrl;
    }


    /**
     * 短网址转长网址
     *
     * @param shortUrl
     * @return
     */
    public String lookupLong(String shortUrl) {
        return "";
    }

}
