package com.lingdonge.spider.webmagic;

/**
 * Interface for identifying different tasks.<br>
 */
public interface Task {

    /**
     * unique id for a task.
     *
     * @return uuid
     */
    String getUUID();

    /**
     * site of a task
     *
     * @return site
     */
    Site getSite();

}
