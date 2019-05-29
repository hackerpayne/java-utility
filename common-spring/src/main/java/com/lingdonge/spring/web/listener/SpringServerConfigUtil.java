package com.lingdonge.spring.web.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 通过监听器获取基本配置信息
 */
@Component
@Slf4j
public class SpringServerConfigUtil implements ApplicationListener<WebServerInitializedEvent> {

    private int serverPort;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.serverPort = event.getWebServer().getPort();
    }

    /**
     * 获取URL地址
     *
     * @return
     */
    public String getUrl() {
        return "http://" + getHostAddress() + ":" + this.serverPort;
    }

    /**
     * 获取本机IP地址
     *
     * @return
     */
    public String getHostAddress() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }
        return address.getHostAddress();
    }

    /**
     * 获取已经开启的版本号信息
     *
     * @return
     */
    public int getPort() {
        return this.serverPort;
    }
}
