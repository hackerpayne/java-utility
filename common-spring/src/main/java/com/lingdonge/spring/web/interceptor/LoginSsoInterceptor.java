//package com.kyle.springutils.web.interceptor;
//
//import com.kyle.springutils.threads.ThreadLocalSsoLoginUtil;
//import com.kyle.springutils.token.AccessToken;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//public class LoginSsoInterceptor implements HandlerInterceptor {
//
//    /**
//     * Redis的Key前缀，保存Token时需要用到
//     */
//    private String redisKeyPrefix = "user:token";
//
//    public LoginSsoInterceptor() {
//
//    }
//
//    public LoginSsoInterceptor(String redisKeyPrefix) {
//        this.redisKeyPrefix = redisKeyPrefix;
//    }
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // token存在header中
//        String token = request.getHeader("token");
//
//        // 1、如果token信息不存在，则证明用户尚未登录，让用户去登录。
//        if (StringUtils.isBlank(token)) {
//            HttpRespUtil.respContent(response, R.error(PublicResultConstant.INVALID_PARAM_EMPTY.result, PublicResultConstant.INVALID_PARAM_EMPTY.msg));
//            return false;
//        }
//        ThreadLocalSsoLoginUtil.setAccessToken(token);
//
//        // 2、根据token从redis获取用户信息是否存在登录
//        AccessToken accessToken = (AccessToken) redisTemplate.opsForValue().get(redisKeyPrefix + token);
//        if (accessToken == null) { // 未登陆或者已经过期了。
//            HttpRespUtil.respContent(response, R.error(PublicResultConstant.UNAUTHORIZED.result, PublicResultConstant.UNAUTHORIZED.msg));
//            return false;
//        }
//
//        return true;
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        ThreadLocalSsoLoginUtil.remove();
//    }
//
//}
