package com.lingdonge.spider.webmagic.pipeline;

import com.lingdonge.spider.webmagic.ResultItems;
import com.lingdonge.spider.webmagic.Task;

/**
 * Pipeline is the persistent and offline process part of crawler.<br>
 * The interface Pipeline can be implemented to customize ways of persistent.
 */
public interface Pipeline {

    /**
     * Process extracted results.
     *
     * @param resultItems resultItems
     * @param task        task
     */
    void process(ResultItems resultItems, Task task);
}
