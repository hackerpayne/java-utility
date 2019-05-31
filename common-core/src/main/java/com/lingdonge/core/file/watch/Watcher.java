package com.lingdonge.core.file.watch;

import java.nio.file.WatchEvent;

/**
 * 观察者
 *
 * @author Looly
 */
public interface Watcher {
    /**
     * 文件创建时执行的方法
     *
     * @param event 事件
     */
    public void onCreate(WatchEvent<?> event);

    /**
     * 文件修改时执行的方法<br>
     * 文件修改可能触发多次
     *
     * @param event 事件
     */
    public void onModify(WatchEvent<?> event);

    /**
     * 文件删除时执行的方法
     *
     * @param event 事件
     */
    public void onDelete(WatchEvent<?> event);

    /**
     * 事件丢失或出错时执行的方法
     *
     * @param event 事件
     */
    public void onOverflow(WatchEvent<?> event);
}
