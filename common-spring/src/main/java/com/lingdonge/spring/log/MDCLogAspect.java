package com.lingdonge.spring.log;

import com.lingdonge.core.dates.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Aspect
public class MDCLogAspect {

//    private static final Logger log = LoggerFactory.getLogger(MDCLogAspect.class);

    private static long STIME = SystemClock.now();//线程安全的时间戳

    private static String REQUESTURL = "url";

    @Pointcut("execution(public * com.demo..*.*.controller.*.*(..))")
    public void log() {
    }

    @Before("log()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        STIME = SystemClock.now();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Map<String, String[]> params = request.getParameterMap();
        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }

        if (!StringUtils.isEmpty(queryString)) {
            queryString = queryString.substring(0, queryString.length() - 1);
        }
        REQUESTURL = request.getRequestURL().toString();

        log.info("url：{}，req：{}", REQUESTURL, queryString);
        MDC.put(LogPreKeyConstants.LOGTYPE, LogTypeEnum.BIZ.getKey());
    }

    @AfterReturning(returning = "ret", pointcut = "log()")
    public void doAfterReturning(Object ret) throws Throwable {
        MDC.put(LogPreKeyConstants.LOGTYPE, LogTypeEnum.RESP.getKey());
        log.info("url：{}，resp：{}", REQUESTURL, ret);
        MDC.remove(LogPreKeyConstants.LOGTYPE);
        MDC.put(LogPreKeyConstants.LOGTYPE, LogTypeEnum.BIZ.getKey());
    }

    /**
     * 后置异常通知
     *
     * @param jp
     */
    @AfterThrowing("log()")
    public void throwss(JoinPoint jp) {
        String costtime = SystemClock.now() - STIME + "ms";
        log.info("url：{}，executetime：{}", REQUESTURL, costtime);
    }

    /**
     * 后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
     *
     * @param jp
     */
    @After("log()")
    public void after(JoinPoint jp) {
        String costtime = SystemClock.now() - STIME + "ms";
        log.info("url：{}，executetime：{}", REQUESTURL, costtime);
    }
}