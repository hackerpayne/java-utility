package com.lingdonge.net.configuration;

import com.lingdonge.net.mail.MailService;
import com.lingdonge.net.mail.MailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

//@Configuration // 配置注解
//@EnableConfigurationProperties(PersonServiceProperties.class) // 开启指定类的配置
@ConditionalOnClass({MailService.class})// 当MailService这个类在类路径中时，且当前容器中没有这个Bean的情况下，开始自动配置
//@ConditionalOnBean(MailMakerConfiguration.Marker.class)
//@ConditionalOnClass({MailService.class, MailProperties.class})// 当MailService这个类在类路径中时，且当前容器中没有这个Bean的情况下，开始自动配置
//@ConditionalOnMissingBean(MailService.class) // 只有对应的ban在系统中都没有被创建，它修饰的初始化代码块才会执行，用户自己手动创建的bean优先
//@ConditionalOnProperty(prefix = "spring.mail", value = "enabled", matchIfMissing = true)// 指定的属性是否有指定的值

@Slf4j
public class MailAutoConfiguration {

//    @Bean
//    @ConditionalOnMissingBean(MailProperties.class)
//    public MailProperties mailProperties() {
//        return new MailProperties();
//    }

    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    @Autowired(required = false)
    private MailProperties mailProperties;

    @Bean
    @ConditionalOnMissingBean(MailService.class)// 当容器中没有指定Bean的情况下，自动配置PersonService类
    public MailService mailService() {

        log.info("<<<<<<<<<<<<<<< 加载MailService邮件服务 >>>>>>>>>>>>>>>>>>");

        MailServiceImpl mailService = new MailServiceImpl(javaMailSender, mailProperties);
        return mailService;
    }

}
