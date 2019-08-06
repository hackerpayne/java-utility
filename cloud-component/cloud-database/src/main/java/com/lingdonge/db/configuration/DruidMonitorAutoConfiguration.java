package com.lingdonge.db.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.lingdonge.db.configuration.properties.DruidProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Druid配置监控自动类
 */
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@EnableConfigurationProperties(DruidProperties.class) // 开启指定类的配置
@Slf4j
public class DruidMonitorAutoConfiguration {

    public DruidMonitorAutoConfiguration() {
        log.info("加载[{}]类", this.getClass().getSimpleName());
    }

    @Resource
    private DruidProperties properties;

    /**
     * 注册一个StatViewServlet
     * Druid内置提供了一个StatViewServlet用于展示Druid的统计信息。
     * WebStatFilter用于采集web-jdbc关联监控的数据。
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean druidStatViewServlet() {
        //org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
//        servletRegistrationBean.setServlet(new StatViewServlet());
//        servletRegistrationBean.addUrlMappings("/druid/*");

        //添加初始化参数：initParams

        //白名单：
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1");

        //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
        servletRegistrationBean.addInitParameter("deny", "192.168.1.73");

        //登录查看信息的账号密码.
        if (StringUtils.isNotEmpty(properties.getDruidUser())) {
            servletRegistrationBean.addInitParameter("loginUsername", properties.getDruidUser());
        }
        if (StringUtils.isNotEmpty(properties.getDruidPass())) {
            servletRegistrationBean.addInitParameter("loginPassword", properties.getDruidPass());
        }

        //是否能够重置数据(禁用HTML页面上的“Reset All”功能)
        servletRegistrationBean.addInitParameter("resetEnable", "false");

        if (StringUtils.isNoneEmpty(properties.getLogSlowSql()) && properties.getLogSlowSql().equals("true")) {
            servletRegistrationBean.addInitParameter("logSlowSql", "true");
        } else {
            servletRegistrationBean.addInitParameter("logSlowSql", "false");
        }
        return servletRegistrationBean;
    }

    /**
     * 注册一个：filterRegistrationBean
     * WebStatFilter用于采集web-jdbc关联监控的数据
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());

        //添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");

        //添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
//        filterRegistrationBean.addInitParameter("profileEnable", "true");
        return filterRegistrationBean;
    }


    //    @Bean
    public WallConfig wallConfig() {
        WallConfig config = new WallConfig();
        config.setMultiStatementAllow(true);//允许一次执行多条语句
        config.setNoneBaseStatementAllow(true);//允许非基本语句的其他语句
        return config;
    }

    //    @Bean
    public WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig());
        return wallFilter;
    }

}
