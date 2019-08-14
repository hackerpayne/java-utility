package com.lingdonge.spring.eventbus;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventBusFacade {

    private final static EventBus eventBus = new EventBus();

    public static void post(BaseEvent event) {
        execute(event);
    }

    /**
     * 发送事件
     * @param event
     */
    public static void execute(BaseEvent event) {
        if(null == event){
            return ;
        }
        eventBus.post(event);
    }

    /**
     * 注册事件
     * @param handler
     */
    public static void register(EventAdapter<? extends BaseEvent> handler) {
        if(null == handler){
            return ;
        }
        eventBus.register(handler);
        log.info("Registered eventAdapter class: {}", handler.getClass());
    }

    /**
     * 反注册事件
     * @param handler
     */
    public static void unregister(EventAdapter<? extends BaseEvent> handler) {
        if(null == handler){
            return ;
        }
        eventBus.unregister(handler);
        log.info("Unregisted eventAdapter class: {}", handler.getClass());
    }

}
