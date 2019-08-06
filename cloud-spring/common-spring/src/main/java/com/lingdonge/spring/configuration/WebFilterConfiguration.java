package com.lingdonge.spring.configuration;

import com.lingdonge.spring.web.filter.BodyReaderFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

//@Configuration
public class WebFilterConfiguration {

    /**
     * BodyReaderFilter替换默认的HttpRequest对象，以实现流的重复使用
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<BodyReaderFilter> Filters() {
        FilterRegistrationBean<BodyReaderFilter> registrationBean = new FilterRegistrationBean<BodyReaderFilter>();
        registrationBean.setFilter(new BodyReaderFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("bodyReaderFilter");
        return registrationBean;
    }

}
