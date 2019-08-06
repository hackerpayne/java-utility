package com.lingdonge.springcloud.configuration;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.lingdonge.spring.configuration.properties.SwaggerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Zuul支持
 */
@EnableConfigurationProperties(SwaggerProperties.class) // 开启指定类的配置
@EnableSwagger2
@EnableSwaggerBootstrapUI // 使用增强UI
//@ConditionalOnExpression("${swagger.enable:true}") // 生产环境配置
@Slf4j
public class SwaggerZuulAutoConfiguration implements SwaggerResourcesProvider {

    @Autowired
    private RouteLocator routeLocator;

    /**
     * 这里继承SwaggerResourcesProvider接口是实现聚合api的关键，另外通过RouteLocator类获取路由列表是实现自动聚合的关键。
     *
     * @return
     */
    @Override
    public List<SwaggerResource> get() {
        //利用routeLocator动态引入微服务
        List<SwaggerResource> resources = new ArrayList<>();
        resources.add(swaggerResource("zuul-gateway", "/v2/api-docs", "1.0"));
        //循环 使用Lambda表达式简化代码
        routeLocator.getRoutes().forEach(route -> {
            //动态获取
            resources.add(swaggerResource(route.getId(), route.getFullPath().replace("**", "v2/api-docs"), "1.0"));
        });
        //也可以直接 继承 Consumer接口
//		routeLocator.getRoutes().forEach(new Consumer<Route>() {
//
//			@Override
//			public void accept(Route t) {
//
//			}
//		});
        return resources;
    }

    /**
     * @param name
     * @param location
     * @param version
     * @return
     */
    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }

}
