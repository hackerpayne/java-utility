//package com.kyle.springutils.configuration;
//
//import lombok.extern.slf4j.Slf4j;
//import org.eclipse.jetty.token.thread.QueuedThreadPool;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
//import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * spring 托管bean管理
// *
// */
////@Configuration
////@ConditionalOnProperty(name = "jetty.enabled")// 必须开启jetty.set才会使用此配置
//@Slf4j
//public class JettyAutoConfiguration {
//
//    /**
//     * 优化jetty容器
//     * @param jettyServerCustomizer
//     * @return
//     */
//    @Bean
//    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory(
//            JettyServerCustomizer jettyServerCustomizer) {
//        JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();
//        factory.addServerCustomizers(jettyServerCustomizer);
//        log.info("<<<<<<<<<<<<<<< 优化jetty容器 >>>>>>>>>>>>>>>>>>");
//        return factory;
//    }
//
//
//    /**
//     * 设置jetty线程池
//     * @return
//     */
//    @Bean
//    public JettyServerCustomizer jettyServerCustomizer() {
//        return server -> {
//            // Tweak the connection configuration used by Jetty to handle incoming HTTP
//            // connections
//            final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
//            threadPool.setMaxThreads(100);
//            threadPool.setMinThreads(20);
//        };
//    }
//
//}
