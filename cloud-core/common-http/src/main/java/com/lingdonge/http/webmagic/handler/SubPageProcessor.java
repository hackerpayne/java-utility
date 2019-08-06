package com.lingdonge.http.webmagic.handler;

import com.lingdonge.http.webmagic.Page;

/**
 */
public interface SubPageProcessor extends RequestMatcher {

    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param page page
     * @return whether continue to match
     */
    MatchOther processPage(Page page);

}
