package com.lingdonge.spring.eventbus;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

/**
 * 所有Event处理类都要继承此Adapter类 实现process进行相关event处理
 *
 * @param <E>
 */
@Slf4j
public abstract class EventAdapter<E extends BaseEvent> {

    private static final String METHOD_NAME = "process";

    @Subscribe
    @SuppressWarnings("all")
    public void onEvent(BaseEvent event) {
        if (ReflectionUtils.findMethod(this.getClass(), METHOD_NAME, event.getClass()) != null) {
            try {
                if (!process((E) event)) {
                    log.warn("handle event {} fail", event.getClass());
                }
            } catch (Exception e) {
                log.error(String.format("handle event %s exception", event.getClass()), e);
            }
        }
    }

    public abstract boolean process(E e);

}