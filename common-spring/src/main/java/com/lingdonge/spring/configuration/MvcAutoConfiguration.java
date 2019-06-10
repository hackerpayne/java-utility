package com.lingdonge.spring.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingdonge.spring.web.converter.CustomNullStringSerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * MVC 通用配置
 */
//@Configuration
//@ConditionalOnProperty(name = "mvc.enabled")// 必须开启nvc.set才会使用此配置
@Slf4j
public class MvcAutoConfiguration extends WebMvcConfigurerAdapter {

    /**
     * 自定义JSON输入输出格式
     *
     * @return
     */
    @Bean
    public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper om = new ObjectMapper();
        om.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);// 开启将输出没有JsonView注解的属性，false关闭将输出有JsonView注解的属性
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);// 配置该objectMapper在反序列化时，忽略目标对象没有的属性。凡是使用该objectMapper反序列化时，都会拥有该特性。
        om.setSerializerProvider(new CustomNullStringSerializerProvider());
        jsonConverter.setObjectMapper(om);
        log.info("<<<<<<<<<<<<<<< 自定义JSON输入输出格式 >>>>>>>>>>>>>>>>>>");
        return jsonConverter;
    }

    /**
     * 扩展输出
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(customJackson2HttpMessageConverter());
    }

}
