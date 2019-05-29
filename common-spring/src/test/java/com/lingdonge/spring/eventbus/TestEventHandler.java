package com.lingdonge.spring.eventbus;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestEventHandler extends EventAdapter<TestEvent> {

    @Override
    public boolean process(TestEvent e) {
        log.info("==================== 收到测试事件 ===================");
        log.info("==================== " + e.getName() + " ===================");
        return true;
    }

}

