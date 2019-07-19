package com.lingdonge.spring.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

//@Configuration
public class JacksonAutoConfiguration {

    /**
     * DateTime格式化字符串
     */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Date格式化字符串
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Time格式化字符串
     */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * jackson2 json序列化 null字段输出为空串
     *
     * @return
     * @author zhaoyang10
     */
    @Bean
    @Primary
    //@ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //不再做统一处理 Springcloud2 fegin 会报日期格式化错误 格式化的日期字段直接加@JsonFormat 注解处理
        //序列化日期格式
        //javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        //javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        //javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer());

        //反序列化日期格式
        //javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        //javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        //javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());

        objectMapper.registerModule(javaTimeModule);

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        return objectMapper;
    }


    /**
     * 注入Jackson，以支持日期转换和中文
     * 注意：如果想要单个bean的某个日期字段显示年月日时分秒的话，只需要在对应日期的get方法上添加@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")即可。
     *
     * @return
     */
    @Bean
    public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

        // 解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        // 解决jackson2无法反序列化LocalDateTime的问题
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule());

        // 序列换成json时,将所有的long变成string,因为js中得数字类型不能包含所有的java long值
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        om.registerModule(simpleModule);

        mappingJackson2HttpMessageConverter.setObjectMapper(om);

        //设置中文编码格式
        List<MediaType> list = new ArrayList<MediaType>();
        list.add(MediaType.APPLICATION_JSON_UTF8);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);

        return mappingJackson2HttpMessageConverter;
    }


    /**
     * 自定义Bean
     *
     * @return
     */
    @Bean
    @Primary
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(DATETIME_FORMATTER))
                .serializerByType(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER))
                .serializerByType(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(DATETIME_FORMATTER))
                .deserializerByType(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER))
                .deserializerByType(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));
    }


}
