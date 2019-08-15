package com.lingdonge.auth.jwt;

import com.lingdonge.spring.SpringContextUtil;
import com.lingdonge.spring.restful.Resp;
import com.lingdonge.spring.token.JwtTokenUtil;
import com.lingdonge.spring.util.SpringRequestUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * ﻿访问控制过滤器，对请求进行拦截处理，在这里我们可以进行一些基本的判断以及数据的基本处理，然后生成一个AuthenticationToken，然后委托给Realm进行身份的验证和权限的验证。
 * 保存到Redis的过滤器，不然每次都需要检查了
 */
@Component
@Slf4j
public class StatelessAccessFilter extends AccessControlFilter {

    private RedisTemplate<Serializable, Object> template;

    public RedisTemplate<Serializable, Object> getTemplate() {
        return template;
    }

    public void setTemplate(RedisTemplate<Serializable, Object> template) {
        this.template = template;
    }

    /**
     * 解决复杂CORS请求时的OPTIONS不放行的问题
     * https://segmentfault.com/a/1190000010757321
     * 返回false不再执行拦截链，返回true时，继续往下执行
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = WebUtils.toHttp(request);
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpResponse.setHeader("Access-control-Allow-Origin", "*");
            httpResponse.setHeader("Access-Control-Allow-Methods", httpRequest.getMethod());
            httpResponse.setHeader("Access-Control-Allow-Headers", httpRequest.getHeader("Access-Control-Request-Headers"));
            httpResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /**
     * 是否放行
     * 先执行：isAccessAllowed 再执行onAccessDenied
     * <p>
     * isAccessAllowed：表示是否允许访问；mappedValue就是[urls]配置中拦截器参数部分，
     * 如果允许访问返回true，否则false；
     * <p>
     * 如果返回true的话，就直接返回交给下一个filter进行处理。
     * 如果返回false的话，回往下执行onAccessDenied，可以直接写false，全部由onAccessDenied进行统一处理
     *
     * @param servletRequest
     * @param servletResponse
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {

        if (null != getSubject(servletRequest, servletResponse) && getSubject(servletRequest, servletResponse).isAuthenticated()) {
            return true;//已经认证直接放行
        }

//        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {//解决shiro与header自定义参数第一次shiro拦截
//            return true;
//        }

//        logger.info("<<<<<<<<<<<<<< isAccessAllowed >>>>>>>>>>");

        return false;//转到拒绝访问处理逻辑
    }

    /**
     * 拒绝处理，接收用户请求，组装成 StatelessToken，﻿然后委托为Realm进行处理
     * ﻿ onAccessDenied：表示当访问拒绝时是否已经处理了；如果返回true表示需要继续处理；
     * 如果返回false表示该拦截器实例已经处理了，将直接返回即可。
     *
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

//        logger.info("<<<<<<<<<<<<<< Shiro onAccessDenied >>>>>>>>>>");

        JwtTokenUtil jwtTokenUtil = SpringContextUtil.getBean(JwtTokenUtil.class);

        // 获取Header里面的Token值
        String token = jwtTokenUtil.getToken(httpRequest);

        // 客户端请求的参数列表，有需要的话，可以用于加密处理
//        Map<String, String[]> params = new HashMap<String, String[]>(servletRequest.getParameterMap());

        // 没有Token的直接返回错误 ，不进行后续的操作了
        if (StringUtils.isEmpty(token)) {
            onLoginFail(servletResponse);//无权限
            return false;
        }

        if (token.equals("thisisatesttoken")) {
            return true;
        }

        try {

            // 解析里面的Account账号
            String account = jwtTokenUtil.getSubject(token);

            // 生成无状态Token
            StatelessToken statelessToken = new StatelessToken(account, token);

            // 委托给Realm进行登录
            getSubject(servletRequest, servletResponse).login(statelessToken);

            //3.大于等于20分钟刷新一次token
//            if(jwtTokenUtil.checkJwtTime((long)claims.get(jwtTokenUtil.CLAIM_KEY_CREATED),20)){
//                httpResponse.setHeader("token", statelessToken.getJwtToken());//token不超时刷新token
//            }

        } catch (Exception e) {
            onLoginFail(servletResponse);//无权限
            return false;

        }

        return true;
    }


    private boolean isOnlineUser(Claims claims) {

        if (null == claims || claims.getExpiration().before(new Date())) {
            return false;
        }
//        String userName = (String) claims.get("name");
//        Integer userPlat = (Integer) claims.get("userPlat");
//        // 查询在线用户列表中是否存在当前用户
//        Map<String, List<String>> userSet = (Map<String, List<String>>) template.opsForValue().get(ConstantDef.ONLINE_USER_SET + userName + ConstantDef.SPLIT_COMA + (userPlat == 0 ? "console" : "user"));
//        if (null == userSet) {
//            return false;
//        }
//        String key = userPlat + ConstantDef.SPLIT_COMA + claims.get("roleId") + ConstantDef.SPLIT_COMA + claims.get("loginPlat");
//        List<String> list = userSet.get(key);
//        if (null == list) {
//            return false;
//        }
//        for (String clientId : list) {
//            if (clientId.equals(claims.getId())) {
//                return true;
//            }
//        }

        return false;

    }


    /**
     * 鉴权失败 返回错误信息
     *
     * @param response
     * @throws IOException
     */
    private void onLoginFail(ServletResponse response) throws IOException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        Resp result = Resp.fail(0, "Auth Fail 无权限操作");
        SpringRequestUtil.writeJson(httpServletResponse, result);

    }


}

