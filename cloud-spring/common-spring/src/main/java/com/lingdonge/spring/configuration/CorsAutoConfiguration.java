package com.lingdonge.spring.configuration;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.lingdonge.spring.configuration.properties.CorsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * 全局跨域设置，使用时：
 * 使用示例：
 * cors:
 * enabled: true
 * methods: GET,POST
 * domains: www.s.com
 * paths:
 */
//@Configuration
@EnableConfigurationProperties(CorsProperties.class) // 开启指定类的配置
//@ConditionalOnBean(CORSMakerConfiguration.Marker.class) //只有在开启注解的时候才会使用
//@ConditionalOnProperty(name = "cors.enabled")// 必须开启cors.set才会使用此配置
@ConditionalOnWebApplication //必须是Web项目
@Slf4j
public class CorsAutoConfiguration {

    @Autowired
    private CorsProperties properties;

    /**
     * 不用nginx来实现反向代理(前后端分离)支持第三方项目跨域引用
     * 简单跨域就是GET，HEAD和POST请求，但是POST请求的"Content-Type"只能是application/x-www-form-urlencoded, multipart/form-data 或 text/plain
     * 反之，就是非简单跨域，此跨域有一个预检机制，说直白点，就是会发两次请求，一次OPTIONS请求，一次真正的请求
     *
     * @return
     */
//    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        // 允许cookies跨域
        config.setAllowCredentials(true);
        // 允许向该服务器提交请求的URI，*表示全部允许。。这里尽量限制来源域，比如http://xxxx:8080 ,以降低安全风险。。
        config.addAllowedOrigin("*");
        // 允许访问的头信息,*表示全部
        config.addAllowedHeader("*");
        // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        config.setMaxAge(18000L);
        // 允许提交请求的方法，*表示全部允许，也可以单独设置GET、PUT等
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
//    @ConditionalOnMissingBean // 当容器中没有指定Bean的情况下，自动配置PersonService类
//    @ConditionalOnProperty(prefix = "cors.enable", value = "true", havingValue = "true", matchIfMissing = true)
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                log.info("<<<<<<<<<<<<<<< 重写CORS跨域处理机制 >>>>>>>>>>>>>>>>>>");

                // 先所域名domain列进来
                List<String> listDomains = Lists.newArrayList();

                if (StringUtils.isNotEmpty(properties.getDomains())) {
                    listDomains = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(properties.getDomains());
                } else {
                    listDomains.add("*");
                }

                List<String> allowMethods = Lists.newArrayList("GET", "POST", "DELETE", "PUT", "OPTIONS");
                if (StringUtils.isNotEmpty(properties.getMethods())) {
                    allowMethods = Splitter.on(",").trimResults().splitToList(properties.getMethods());
                }

                // 处理path的列表
                List<String> listPaths = Lists.newArrayList();
                if (StringUtils.isNotEmpty(properties.getPaths())) {
                    listPaths = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(properties.getPaths());
                } else { //添加所有路径
                    listPaths.add("/**");
                }

//                logger.info("CORS域名列表：" + Joiner.on(",").join(listDomains.toArray(new String[0])));
//                logger.info("CORS方法列表：" + Joiner.on(",").join(allowMethods.toArray(new String[0])));
//                logger.info("CORS路径列表：" + Joiner.on(",").join(listPaths.toArray(new String[0])));

                for (Integer i = 0, total = listPaths.size(); i < total; i++) {
                    registry.addMapping(listPaths.get(i))
                            .allowedOrigins(listDomains.toArray(new String[0]))
                            .allowedMethods(allowMethods.toArray(new String[0]))
                            .allowedHeaders("Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With,token,origin, Accept")
                            .maxAge(3600)
                            .allowCredentials(false);

                }

//                registry.addMapping("/**")
//                        .allowedOrigins(listDomains.toArray(new String[0]))
//                        .allowedMethods(allowMethods.toArray(new String[0]))
//                        .allowCredentials(false)
//                        .maxAge(3600)
//                ;

//				registry.addMapping("/api/**");
//                registry.addMapping("/**")
//                        .allowedOrigins("http://domain.com", "http://domain2.com")
//                        .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
//                        .allowCredentials(false).maxAge(3600);
            }
        };
    }
}