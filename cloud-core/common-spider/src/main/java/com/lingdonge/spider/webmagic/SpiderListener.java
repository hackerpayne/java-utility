package com.lingdonge.spider.webmagic;

/**
 * Listener of Spider on page processing. Used for monitor and such on.
 */
public interface SpiderListener {

    void onSuccess(Request request);

    void onError(Request request);
}
