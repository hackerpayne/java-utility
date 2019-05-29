package com.lingdonge.spring.configuration;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import com.lingdonge.spring.configuration.properties.RestTemplateProperties;
import com.lingdonge.spring.http.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * * 调用 RestTemplate 的默认构造函数，RestTemplate 对象在底层通过使用 java.net 包下的实现创建 HTTP 请求. 这种情况相对简单, 一个请求一个连接, 也没有限制.
 * <p>
 * 在 keepAlive(默认) 情况下, 默认设置了对单个 host 的最大 connection 数量是 5. 所有connection数量不能超过 10~ 如果说单个 host 设置长连接数设置为5问题不大的话, 那么对于需要访问多个不同网站的情况下, 连接总数一共10个就有点坑了. 在长时间得不到Connection的情况下, 会抛出异常:
 */
//@Configuration
@ConditionalOnClass(RestTemplateBuilder.class)
@EnableConfigurationProperties(RestTemplateProperties.class)
//@ConditionalOnBean(RestTemplateMakerConfiguration.Marker.class)
@Slf4j
public class RestTemplateAutoConfiguration {


    @Autowired
    private RestTemplateProperties restTemplateProperties;

    /**
     * 使用FastJson做为转换器
     *
     * @return
     */
    @Bean
    public HttpMessageConverter fastJsonHttpMessageConverter() {
        //MediaType
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);

        //FastJsonConfig
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue, SerializerFeature.QuoteFieldNames);

        //创建FastJsonHttpMessageConverter4    Spring 4.2后使用
        FastJsonHttpMessageConverter4 fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter4();
        fastJsonHttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);

        return fastJsonHttpMessageConverter;
    }

    /**
     * 创建默认的RestTemplate配置
     *
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {

        log.info("<<<<<<<<<<<<<<< 重写 RestTemplate 处理机制 >>>>>>>>>>>>>>>>>>");

        //
//        return restTemplateBuilder
//                .setConnectTimeout(500)
//                .setReadTimeout(500)
//                .build();

        // 添加转换器
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        messageConverters.add(new FormHttpMessageConverter());
//        messageConverters.add(new MappingJackson2HttpMessageConverter());
        messageConverters.add(fastJsonHttpMessageConverter());// 留一个JOSN转换器即可

        RestTemplate restTemplate = new RestTemplate(messageConverters);
        restTemplate.setRequestFactory(clientHttpRequestFactory(restTemplateProperties));
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

        return restTemplate;
    }

    /**
     * 创建单请求实例
     *
     * @param properties
     * @return
     */
    public SimpleClientHttpRequestFactory getSimpleHttpClient(RestTemplateProperties properties) {

        int timeoutMills = properties.getTimeOutSeconds() * 1000;

        // 方法一：只设置超时，不设置并发
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setReadTimeout(timeoutMills);//ms
        simpleClientHttpRequestFactory.setConnectTimeout(timeoutMills);//ms

        return simpleClientHttpRequestFactory;
    }

    /**
     * 设置并发和线程
     *
     * @return
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(RestTemplateProperties restTemplateProperties) {

        log.info("<<<<<<<<<<<<<<< 重写 RestTemplate RequestFactory 处理机制，超时时间设为[" + restTemplateProperties.getTimeOutSeconds() + "]秒 >>>>>>>>>>>>>>>>>>");

        int timeoutMills = restTemplateProperties.getTimeOutSeconds() * 1000;

        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();

        // 方法二：设置超时时间和并发设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeoutMills)
                .setConnectionRequestTimeout(timeoutMills)
                .setSocketTimeout(timeoutMills)
                .build();

        //最大并发数
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(manager)
                .setDefaultRequestConfig(requestConfig)
                .setMaxConnPerRoute(restTemplateProperties.getMaxPerRoute())
                .setMaxConnTotal(restTemplateProperties.getMaxTotal())
                .setDefaultRequestConfig(requestConfig)
                .build();


        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return httpRequestFactory;
    }

    /**
     * 异步的超时机制设置
     *
     * @return
     */
    @Bean
    public AsyncClientHttpRequestFactory asyncClientHttpRequestFactory() {

        int timeoutMills = restTemplateProperties.getTimeOutSeconds() * 1000;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(timeoutMills);
        factory.setReadTimeout(timeoutMills);
        return factory;

    }

    /**
     * AsyncRestTemplate通过回调机制能够很好地异步处理多个http请求，使得客户端在主方法中不必等待服务器响应，而是继续执行后续代码，这样就较大地提高了代码的执行效率，减少响应时间。
     *
     * @return
     */
    @Bean
    public AsyncRestTemplate asyncRestTemplate() {

        log.info("<<<<<<<<<<<<<<< 重写 AsyncRestTemplate 处理机制，超时时间设为[" + restTemplateProperties.getTimeOutSeconds() + "]秒 >>>>>>>>>>>>>>>>>>");

        return new AsyncRestTemplate(asyncClientHttpRequestFactory());
    }

    /**
     * 创建自己的Rest服务代码
     *
     * @return
     */
    @Bean
    public RestTemplateUtil restTemplateUtil() {
        return new RestTemplateUtil(restTemplate());
    }
}
