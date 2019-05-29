package com.lingdonge.spring.configuration;

import com.lingdonge.spring.SpringContextUtil;
import org.springframework.context.annotation.Import;

/**
 * 添加自动加载SpringUtil的服务，不需要任何条件，引用了本项目都会自动加载
 */
//@Configuration
//@ConditionalOnClass(SpringContextUtils.class)
@Import(SpringContextUtil.class)
public class SpringUtilsAutoConfiguration {

//    private Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Bean
//    public SpringContextUtils springContextUtils() {
//        logger.info("<<<<<<<<<<<<<<< 加载 SpringContextUtils 服务 >>>>>>>>>>>>>>>>>>");
//        return new SpringContextUtils();
//    }
}