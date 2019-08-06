package com.lingdonge.http.webmagic.scheduler;

import com.lingdonge.core.http.HttpConstant;
import com.lingdonge.http.webmagic.Request;
import com.lingdonge.http.webmagic.Task;
import com.lingdonge.http.webmagic.scheduler.component.DuplicateRemover;
import com.lingdonge.http.webmagic.scheduler.component.HashSetDuplicateRemover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Remove duplicate urls and only com.lingdonge.push urls which are not duplicate.<br><br>
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public abstract class DuplicateRemovedScheduler implements Scheduler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private DuplicateRemover duplicatedRemover = new HashSetDuplicateRemover();

    public DuplicateRemover getDuplicateRemover() {
        return duplicatedRemover;
    }

    public DuplicateRemovedScheduler setDuplicateRemover(DuplicateRemover duplicatedRemover) {
        this.duplicatedRemover = duplicatedRemover;
        return this;
    }

    /**
     * @param request request
     * @param task    task
     */
    @Override
    public void push(Request request, Task task) {
        logger.trace("get a candidate url {}", request.getUrl());

        // 如果 重试、POST、没采集过，这3种情况，都会添加到采集队列里面
        if (shouldReserved(request) || noNeedToRemoveDuplicate(request) || !duplicatedRemover.isDuplicate(request, task)) {
            logger.debug("com.lingdonge.push to queue {}", request.getUrl());
            pushWhenNoDuplicate(request, task);
        }
    }

    protected boolean shouldReserved(Request request) {
        return request.getExtra(Request.CYCLE_TRIED_TIMES) != null;
    }

    protected boolean noNeedToRemoveDuplicate(Request request) {
        return HttpConstant.Method.POST.equalsIgnoreCase(request.getMethod());
    }

    protected void pushWhenNoDuplicate(Request request, Task task) {

    }

    protected void clearItem(Request request, Task task) {

    }


}
