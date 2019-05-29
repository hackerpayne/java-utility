package com.lingdonge.spring.eventbus;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestLaunch {

    @Test
    public void testExecute() {
        EventBusFacade.execute(new TestEvent("aaaaa")); //发布事件
    }

}