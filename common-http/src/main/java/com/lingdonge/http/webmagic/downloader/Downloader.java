package com.lingdonge.http.webmagic.downloader;

import com.lingdonge.http.webmagic.Page;
import com.lingdonge.http.webmagic.Request;
import com.lingdonge.http.webmagic.Task;

/**
 * Downloader is the part that downloads web pages and store in Page object. <br>
 * Downloader has {@link #setThread(int)} method because downloader is always the bottleneck of a crawler,
 * there are always some mechanisms such as pooling in downloader, and pool size is related to thread numbers.
 */
public interface Downloader {

    /**
     * Downloads web pages and store in Page object.
     *
     * @param request request
     * @param task    task
     * @return page
     */
    Page download(Request request, Task task);

    /**
     * 只下载头部信息回来，仅做分析使用
     * @param request
     * @param task
     * @return
     */
    Page downloadHeader(Request request, Task task);

    /**
     * Tell the downloader how many threads the spider used.
     *
     * @param threadNum number of threads
     */
    void setThread(int threadNum);
}
