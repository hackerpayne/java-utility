package com.lingdonge.spring.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;

/**
 * Jetty 2.x配置
 */
//@Configuration
//@ConditionalOnProperty(name = "jetty.enabled")// 必须开启jetty.set才会使用此配置
@Slf4j
public class JettyAutoConfiguration2X {

    /**
     * 优化jetty容器
     *
     * @return
     */
    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
//        factory.setPort(9000);
//        factory.setContextPath("/myapp");
//        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/notfound.html"));
        return factory;
    }

}
