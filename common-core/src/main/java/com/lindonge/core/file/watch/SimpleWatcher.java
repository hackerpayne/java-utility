package com.lindonge.core.file.watch;

import java.nio.file.WatchEvent;

/**
 * 空白WatchListener<br>
 * 用户继承此类后实现需要监听的方法
 * @author Looly
 *
 */
public class SimpleWatcher implements Watcher{

	@Override
	public void onCreate(WatchEvent<?> event) {
	}

	@Override
	public void onModify(WatchEvent<?> event) {
	}

	@Override
	public void onDelete(WatchEvent<?> event) {
	}

	@Override
	public void onOverflow(WatchEvent<?> event) {
	}

}
