package com.lingdonge.redis.publish;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Receiver {
    public void receiveMessage(String message) {
        log.info("Received <" + message + ">");
    }
}