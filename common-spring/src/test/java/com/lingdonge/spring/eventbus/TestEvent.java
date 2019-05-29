package com.lingdonge.spring.eventbus;

public class TestEvent implements BaseEvent {

    private String name;

    public TestEvent() {
    }

    public TestEvent(String a) {
        this.name = a;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

}

