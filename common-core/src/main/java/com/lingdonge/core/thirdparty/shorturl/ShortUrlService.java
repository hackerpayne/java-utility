package com.lingdonge.core.thirdparty.shorturl;

import com.lingdonge.core.thirdparty.shorturl.shorten.ShortenUrlInterface;
import com.lingdonge.core.thirdparty.shorturl.stores.ShortUrlStoreInterface;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ShortUrlService {

    private ShortUrlStoreInterface shortUrlStoreInterface;

    private ShortenUrlInterface shortenUrlInterface;

    /**
     * @param shortUrlStoreInterface
     * @param shortenUrlInterface
     */
    public ShortUrlService(ShortUrlStoreInterface shortUrlStoreInterface, ShortenUrlInterface shortenUrlInterface) {
        this.shortUrlStoreInterface = shortUrlStoreInterface;
        this.shortenUrlInterface = shortenUrlInterface;
    }

    /**
     * 长网址转短网址
     *
     * @param longUrl
     * @return
     */
    public String convertShort(String longUrl) {
        String shortUrl = this.shortUrlStoreInterface.getByLongUrl(longUrl);
        if (StringUtils.isEmpty(shortUrl)) {
            shortUrl = this.shortenUrlInterface.shorten(longUrl);
            if (StringUtils.isEmpty(shortUrl)) {
                log.error("Cannot convert long url to short url");
            } else {
                this.shortUrlStoreInterface.saveShortUrl(longUrl, shortUrl);
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
        return this.shortUrlStoreInterface.getByShortUrl(shortUrl);
    }

}
