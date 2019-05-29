package com.lingdonge.spring.configuration;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.lingdonge.spring.configuration.properties.SwaggerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 正式环境一般是需要关闭的，可根据springboot的多环境配置进行设置
 */
@EnableConfigurationProperties(SwaggerProperties.class) // 开启指定类的配置
@EnableSwagger2
@EnableSwaggerBootstrapUI // 使用增强UI
//@ConditionalOnExpression("${swagger.enable:true}") // 生产环境配置
@Slf4j
public class SwaggerAPIAutoConfiguration {

    @Resource
    private SwaggerProperties swaggerProperties;

    /**
     * @return
     */
    @Bean
    public Docket createRestApi() {

        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("Authentication").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                // 是否开启
                .enable(true).select()
                // 扫描的路径包
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getPackages()))
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(PathSelectors.any())
                .build()
                .pathMapping(swaggerProperties.getPath())
                .globalOperationParameters(pars);
    }

    /**
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle()) // 标题
                .description(swaggerProperties.getDescription()) // 描述
//                .termsOfServiceUrl("http://127.0.0.1:9000")
                .contact(new Contact("kyle", "https://www.lingdonge.com/", "hackerpayne@qq.com")) // 作者信息
                .version(swaggerProperties.getVersion())
                .build();
    }


}
