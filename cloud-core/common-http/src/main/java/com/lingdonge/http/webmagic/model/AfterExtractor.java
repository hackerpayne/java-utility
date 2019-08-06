package com.lingdonge.http.webmagic.model;

import com.lingdonge.http.webmagic.Page;

/**
 * Interface to be implemented by page models that need to do something after fields are extracted.<br>
 */
public interface AfterExtractor {

    void afterProcess(Page page);
}
