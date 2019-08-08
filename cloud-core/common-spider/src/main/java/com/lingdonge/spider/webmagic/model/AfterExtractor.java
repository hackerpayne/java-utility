package com.lingdonge.spider.webmagic.model;

import com.lingdonge.spider.webmagic.Page;

/**
 * Interface to be implemented by page models that need to do something after fields are extracted.<br>
 */
public interface AfterExtractor {

    void afterProcess(Page page);
}
