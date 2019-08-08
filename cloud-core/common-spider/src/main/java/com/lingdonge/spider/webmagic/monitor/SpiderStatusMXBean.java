package com.lingdonge.spider.webmagic.monitor;

import java.util.Date;
import java.util.List;

/**
 *
 */
public interface SpiderStatusMXBean {

    public String getName();

    public String getStatus();

    public int getThread();

    public int getTotalPageCount();

    public int getLeftPageCount();

    public int getSuccessPageCount();

    public int getErrorPageCount();

    public List<String> getErrorPages();

    public void start();

    public void stop();

    public Date getStartTime();

    public int getPagePerSecond();
}
