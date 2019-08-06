package com.lingdonge.spring.token;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import com.lingdonge.spring.configuration.properties.JwtProperties;
import com.lingdonge.spring.bean.token.JwtUserInfo;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Jwt生成Token的基本操作类
 */
@Slf4j
public class JwtTokenUtil {

    /**
     * Jwt配置
     */
    private JwtProperties jwtProperties;

    /**
     * 加密算法，统一使用
     */
    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public JwtTokenUtil() {
        this.jwtProperties = new JwtProperties();
    }

    /**
     * 构造函数
     *
     * @param jwtProperties
     */
    public JwtTokenUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 刷新Token值
     *
     * @param token  原Token
     * @param expire 过期时间重置
     * @return
     */
    public String refreshToken(String token, long expire) {
        String refreshedToken;
        try {
            final Claims jwtUserInfo = this.parseJwt(token); // 解析出用户信息
            refreshedToken = generateToken(jwtUserInfo, expire); // 重新生成一份刷新Token
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 生成不过期的Token
     *
     * @param claims
     * @return
     */
    public String generateToken(Map<String, Object> claims) {
        return generateToken(claims, 0);
    }

    /**
     * 生成token
     *
     * @param claims 加密内容
     * @param expire 过期时间
     * @return
     */
    public String generateToken(Map<String, Object> claims, long expire) {
        return generateToken(claims, expire, "", "");
    }

    /**
     * 生成不过期的Token
     *
     * @param userInfo
     * @return
     */
    public String generateToken(Object userInfo) {
        Map<String, Object> claims = BeanUtil.beanToMap(userInfo);
        return generateToken(claims, 0L, "", "");
    }

    /**
     * 根据实体生成Token
     *
     * @param userInfo
     * @param expire
     * @return
     */
    public String generateToken(Object userInfo, long expire) {
        Map<String, Object> claims = BeanUtil.beanToMap(userInfo);
        return generateToken(claims, expire, "", "");
    }

    /**
     * 生成实体的Token
     *
     * @param userInfo
     * @param expire
     * @param subject
     * @return
     */
    public String generateToken(Object userInfo, long expire, String subject) {
        Map<String, Object> claims = BeanUtil.beanToMap(userInfo);
        return generateToken(claims, expire, subject, "");
    }

    /**
     * 签发JWT
     *
     * @param claims
     * @param expireTime
     * @param subject
     * @param audience
     * @return
     */
    public String generateToken(Map<String, Object> claims, Long expireTime, String subject, String audience) {
        JwtBuilder jwtBuilder = Jwts.builder()
                // .setId("") // 令牌ID
                .setIssuer(this.jwtProperties.getAppName()) //发行者是程序的名称，通用的
                .setClaims(claims) // 签发的内容核心
                .setSubject(subject) //把账号做为subject传进Token里面，subject为用户标识
                .setAudience(audience) //根据设备生成接收者
                .setIssuedAt(now()) // 签发时间，一般是现在
                ;

        // 时间不为0才设置过期策略
        if (expireTime > 0) {
            jwtBuilder.setExpiration(generateExpirationDate(expireTime)); // 签名过期时间
        }

        // 1、把role信息放到UserInfo实体里面生成到Claim里面即可统一管理

        // 2、手动把用户的Role角色和Perm权限添加到Token里面，后面可以解析出来
//        if (StringUtils.isNotBlank(jwtUserInfo.getRoles())) {
//            jwtBuilder.claim("roles", jwtUserInfo.getRoles());
//        }
//        if (StringUtils.isNotBlank(jwtUserInfo.getPerms())) {
//            jwtBuilder.claim("perms", jwtUserInfo.getPerms());
//        }

        return jwtBuilder.compressWith(CompressionCodecs.DEFLATE) // 压缩
                .signWith(SIGNATURE_ALGORITHM, this.jwtProperties.getSecret()) // 签名加密
                .compact(); // 打包生成
    }

    /**
     * 解密Token到Map里面
     *
     * @param token
     * @return 返回NULL表示验证失败
     */
    public Map<String, Object> parseJwtMap(String token) {
        return parseJwt(token);
    }

    /**
     * 解析Jwt到实体里面
     *
     * @param token Token字符串
     * @param clz   解析的目标实体
     * @param <T>
     * @return
     */
    public <T> T parseJwt(String token, Class<T> clz) {
        Map<String, Object> maps = parseJwtMap(token);
        if (MapUtils.isEmpty(maps)) {
            return null;
        }
        return BeanUtil.mapToBean(maps, clz, true);
    }

    /**
     * 取出JwtToken里面的Claim参数
     *
     * @param token
     * @return
     */
    public Claims parseJwt(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(this.jwtProperties.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (Exception e) {
            log.error("Token解析失败", e);
            claims = null;
        }
        return claims;
    }

    /**
     * 验效用户信息是否合法
     *
     * @param token       待校验的Token
     * @param jwtUserInfo 对比的用户信息
     * @return
     */
    public Boolean validateToken(String token, JwtUserInfo jwtUserInfo) {
        final String account = getSubject(token);
//        final Date created = getIssuedAtDateFromToken(token);
        return (
                account != null && account.equals(jwtUserInfo.getUserId()) && !isTokenExpired(token)
        );
    }


    /**
     * 取出Subject对象，可以是手机、账号、用户ID等都可以
     *
     * @param jwt
     * @return
     */
    public String getSubject(String jwt) {
        Claims claims = parseJwt(jwt);
        if (claims != null) {
            return claims.getSubject();
        }
        return null;
    }


    /**
     * 从Token中获取audience
     *
     * @param token
     * @return
     */
    public String getAudience(String token) {
        String audience;
        try {
            final Claims claims = this.parseJwt(token);
            audience = claims.getAudience();
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    /**
     * 获取过期时间
     *
     * @param token
     * @return
     */
    public Date getIssuedAt(String token) {
        Date issueAt;
        try {
            final Claims claims = this.parseJwt(token);
            issueAt = claims.getIssuedAt();
        } catch (Exception e) {
            issueAt = null;
        }
        return issueAt;
    }

    /**
     * 获取设置的token失效时间
     *
     * @param token
     * @return
     */
    public Date getExpiration(String token) {
        Date expiration;
        try {
            final Claims claims = this.parseJwt(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    /**
     * 动态设置指定不同设备的Token过期时间
     *
     * @param expires 过期时间，单位秒
     * @return
     */
    private Date generateExpirationDate(Long expires) {
//        return new Date(Instant.now().toEpochMilli() + expires);
        return new Date(now().getTime() + expires * 1000);
    }

    /**
     * 获取jwt系统当前时间与生成时间超过多少分钟
     *
     * @param begin
     * @return
     */
    private long getJwtMin(long begin) {
        Date exp = new Date();
        long end = exp.getTime();
        long between = (end - begin);
        long day = between / (24 * 60 * 60 * 1000);
        long hour = (between / (60 * 60 * 1000) - day * 24);
        long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
        //long s=(between/1000-day*24*60*60-hour*60*60-min*60);
        //System.out.println(""+day+"天"+hour+"小时"+min+"分"+s+"秒");
        return min;
    }

    /**
     * 是否超过指定时间
     *
     * @param begin 创建时间
     * @param m     超过时间
     * @return
     */
    public boolean checkJwtTime(long begin, int m) {
        long min = getJwtMin(begin);
        return min >= m;
    }

    /**
     * @param created
     * @param lastPasswordReset
     * @return
     */
    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private Date now() {
        return new Date();
    }


    /**
     * 判断token失效时间是否到了
     * True 已经过期
     * False 没有过期
     *
     * @param token
     * @return
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpiration(token);
        return null == expiration || expiration.before(new Date());
    }

    /**
     * 打印所有Header里面的信息
     *
     * @param request
     */
    private void printHeaders(HttpServletRequest request) {
        Enumeration<String> e = request.getHeaderNames();
        while (e.hasMoreElements()) {
            String headerName = e.nextElement();//透明称
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                log.info(headerName + ":" + headerValues.nextElement());
            }
        }
    }

    /**
     * 获取token，从多个地方获取，可以加上自己的自定义头信息
     *
     * @param request
     * @param headerStr
     * @return
     */
    public String getToken(HttpServletRequest request, String... headerStr) {

//        printHeaders(request);

        Set<String> listHeaders = new HashSet<String>(Arrays.asList(headerStr));
        if (CollectionUtils.isEmpty(listHeaders)) {
            listHeaders.addAll(Arrays.asList("Authorization", "Token", "token"));
        }

        final Optional<String> accessToken = listHeaders.stream()
                .filter(item -> StringUtils.isNotEmpty(request.getHeader(item)))
                .map(item -> request.getHeader(item))
                .findFirst();

        if (accessToken.isPresent()) { // 取到了就直接返回了
            return accessToken.get();
        }

        // 还是获取不到再从Cookie中拿
        Cookie[] cookies = request.getCookies();
        final List<String> tokenInCookie = Lists.newArrayList();
        for (Cookie cookie : cookies) {
            listHeaders.forEach(header -> {
                if (cookie.getName().equals(header)) {
                    tokenInCookie.add(cookie.getValue());
                }
            });
        }
        return CollectionUtils.isEmpty(tokenInCookie) ? null : tokenInCookie.get(0);
    }

}
