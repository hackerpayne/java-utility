package com.lingdonge.http.webmagic.handler;

import com.lingdonge.http.webmagic.ResultItems;
import com.lingdonge.http.webmagic.Task;

/**
 *
 */
public interface SubPipeline extends RequestMatcher {

    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param resultItems resultItems
     * @param task        task
     * @return whether continue to match
     */
    MatchOther processResult(ResultItems resultItems, Task task);

}
