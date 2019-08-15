package com.lingdonge.spring;

import com.lingdonge.spring.bean.request.RequestMethodItem;
import com.lingdonge.spring.enums.EnvironmentEnum;
import com.lingdonge.spring.util.ControllerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 自定义Spring工具类
 * 普通类调用Spring bean对象：
 * 1、此类需要放到Application.java同包或者子包下才能被扫描，否则失效。
 * <p>
 * 2、使用时：
 *
 * @Bean public SpringUtilspringUtil2(){return new SpringContextUtils();}
 * 3、如果不在SpringBoot的扫描包下面：
 * @Import(value={SpringContextUtils.class}) 此时：SpringUtil是不需要添加@Component注解
 * <p>
 * 4、XML方式需要加入：<bean id="springContextUtil" class="com.kyle.SpringContextUtils" singleton="true" />
 * 然后使用时： ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml"); 注入
 */
@Component
//@Lazy(false)
@Slf4j
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 设置容器
     *
     * @param context
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;

//        if (SpringContextUtils.applicationContext == null) {
//            SpringContextUtils.applicationContext = applicationContext;
//        }

//        log.info("========ApplicationContext配置成功,applicationContext=" + applicationContext + "========");
        log.info("<<<<<<<<<<<<<<< 加载 SpringApplicationContext >>>>>>>>>>>>>>>>>>");

    }

    /**
     * 获取ApplicationContext对象
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 根据bean的名称获取bean
     *
     * @param name
     * @return
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 根据bean的class来查找对象
     *
     * @param <T>
     * @param c
     * @return
     */
    public static <T> T getBean(Class<T> c) {
        return applicationContext.getBean(c);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 根据bean的class来查找所有的对象（包括子类）
     *
     * @param <T>
     * @param c
     * @return
     */
    public static <T> Map<String, T> getBeans(Class<T> c) {
        return applicationContext.getBeansOfType(c);
    }

    /**
     * 是否包含Bean
     *
     * @param name
     * @return
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 是否单例模式
     *
     * @param name
     * @return
     */
    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    /**
     * 获取Bean的类型
     *
     * @param name
     * @return
     */
    public static Class<?> getType(String name) {
        return applicationContext.getType(name);
    }

    /**
     * 获取Bean的别名
     *
     * @param name
     * @return
     */
    public static String[] getAliases(String name) {
        return applicationContext.getAliases(name);
    }

    /**
     * 国际化使用
     *
     * @param key
     * @return
     */
    public static String getMessage(String key) {
        return applicationContext.getMessage(key, null, Locale.getDefault());
    }

    /**
     * 国际化时使用
     *
     * @param code
     * @param args
     * @param defaultMessage
     * @param locale
     * @return
     */
    public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return applicationContext.getMessage(code, args, defaultMessage, locale);
    }

    /**
     * 国际化时使用
     *
     * @param code
     * @param args
     * @param locale
     * @return
     */
    public static String getMessage(String code, Object[] args, Locale locale) {
        return applicationContext.getMessage(code, args, locale);
    }

    /**
     * 国际化时使用
     *
     * @param resolvable
     * @param locale
     * @return
     */
    public static String getMessage(MessageSourceResolvable resolvable, Locale locale) {
        return applicationContext.getMessage(resolvable, locale);
    }

    /**
     * 获取当前环境
     *
     * @return
     */
    public static String getActiveProfile() {
        return applicationContext.getEnvironment().getActiveProfiles()[0];
    }

    /**
     * 获取环境信息
     *
     * @return Environment
     */
    public static Environment getEnvironment() {
        return applicationContext.getEnvironment();
    }

    /**
     * 根据Profile获取当前的开发环境
     *
     * @return
     */
    public static EnvironmentEnum getEnv() {
        String profile = getActiveProfile().toLowerCase();
        EnvironmentEnum env;
        switch (profile) {
            case "local":
            case "localhost":
                env = EnvironmentEnum.LOCAL;
                break;
            case "test":
                env = EnvironmentEnum.TEST;
                break;
            case "stg":
                env = EnvironmentEnum.STG;
                break;
            case "prod":
                env = EnvironmentEnum.PROD;
                break;
            case "dev":
                env = EnvironmentEnum.DEV;
                break;
            default:
                env = EnvironmentEnum.CUSTOM;
                break;
        }
        return env;
    }

    /**
     * @param event void    返回类型
     * @Title: 发布事件
     */
    public static void publishEvent(Object event) {
        applicationContext.publishEvent(event);
    }

    /**
     * 以单例模式注入一个实体，比如：
     * registerSingleton("foo", new Queue("foo"))
     *
     * @param name
     * @param obj
     */
    public static void registerSingeton(String name, Object obj) {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        configurableApplicationContext.getBeanFactory().registerSingleton(name, obj);
    }

    /**
     * 获取HttpServletRequest
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request;
    }

    /**
     * 获取控制器里面的所有链接
     *
     * @return
     */
    public static Map<RequestMappingInfo, HandlerMethod> getControllerUrlMap() {
//        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class); //获取url与类和方法的对应信息，用Swagger时可能出错。重复获取
        RequestMappingHandlerMapping mapping = (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping"); //获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        return map;
    }

    /**
     * 获取控制器里面的所有链接
     *
     * @return
     */
    public static List<RequestMethodItem> getControllerUrls() {
        RequestMappingHandlerMapping mapping = (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping"); //获取url与类和方法的对应信息
        return ControllerUtil.getAllUrls(mapping);
    }

    /**
     * 清除applicationContext静态变量.
     */
    public static void cleanApplicationContext() {
        applicationContext = null;
    }

    /**
     * 检查注入状态
     */
    private static void checkApplicationContext() {
        if (null == applicationContext) {
            throw new IllegalStateException(
                    "applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHolder");
        }
    }
}
