package com.lingdonge.spider.webmagic.pipeline;

import java.util.List;

/**
 * Pipeline that can collect and store results. <br>
 */
public interface CollectorPipeline<T> extends Pipeline {

    /**
     * Get all results collected.
     *
     * @return collected results
     */
    List<T> getCollected();
}
