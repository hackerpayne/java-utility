package com.lingdonge.http.webmagic.pipeline;

import com.lingdonge.http.webmagic.ResultItems;
import com.lingdonge.http.webmagic.Task;

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
