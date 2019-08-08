package com.lingdonge.spider.webmagic.handler;

import com.lingdonge.spider.webmagic.Request;

/**
 *
 */
public interface RequestMatcher {

    /**
     * Check whether to process the page.<br><br>
     * Please DO NOT change page status in this method.
     *
     * @param page page
     * @return whether matches
     */
    boolean match(Request page);

}
