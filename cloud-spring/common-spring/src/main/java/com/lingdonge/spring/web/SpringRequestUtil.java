package com.lingdonge.spring.web;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.net.HttpHeaders;
import com.lingdonge.core.http.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

@Slf4j
public class SpringRequestUtil extends RequestUtil {

    private final static String staticSuffix = ".css,.js,.png,.jpg,.gif,.jpeg,.bmp,.ico,.swf,.psd,.htc,.htm,.html,.crx,.xpi,.exe,.ipa,.apk,.woff2,.ico,.swf,.ttf,.otf,.svg,.woff";
    /**
     * 静态文件后缀
     */
    private final static String[] staticFiles = StringUtils.split(staticSuffix, ",");

    /**
     * 动态映射URL后缀
     */
    private final static String urlSuffix = ".html";

    public static String[] getStaticFiles() {
        return staticFiles;
    }

    /**
     * 设置 Cookie（生成时间为1天）
     *
     * @param name  名称
     * @param value 值
     */
    public static void setCookie(HttpServletResponse response, String name, String value) {
        setCookie(response, name, value, 60 * 60 * 24);
    }

    /**
     * 设置 Cookie
     *
     * @param name  名称
     * @param value 值
     */
    public static void setCookie(HttpServletResponse response, String name, String value, String path) {
        setCookie(response, name, value, path, 60 * 60 * 24);
    }

    /**
     * 设置 Cookie
     *
     * @param name   名称
     * @param value  值
     * @param maxAge 生存时间（单位秒）
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        setCookie(response, name, value, "/", maxAge);
    }

    /**
     * 设置 Cookie
     *
     * @param name   名称
     * @param value  值
     * @param maxAge 生存时间（单位秒）
     * @param path   路径
     */
    public static void setCookie(HttpServletResponse response, String name, String value, String path, int maxAge) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        try {
            cookie.setValue(URLEncoder.encode(value, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.addCookie(cookie);
    }

    /**
     * 移除cookie
     *
     * @param response
     * @param name
     */
    public static void removeCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setValue(null);
        response.addCookie(cookie);
    }

    /**
     * 获得指定Cookie的值
     *
     * @param name 名称
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, String name) {
        return getCookie(request, null, name, false);
    }

    /**
     * 获得指定Cookie的值，并删除。
     *
     * @param name 名称
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        return getCookie(request, response, name, true);
    }

    /**
     * 获得指定Cookie的值
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param name     名字
     * @param isRemove 是否移除
     * @return 值
     */
    public static String getCookie(HttpServletRequest request, HttpServletResponse response, String name, boolean isRemove) {
        String value = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    try {
                        value = URLDecoder.decode(cookie.getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (isRemove) {
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }
        }
        return value;
    }

    /**
     * 设置客户端缓存过期时间 的Header.
     *
     * @param response
     * @param expiresSeconds
     */
    public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds) {
        // Http 1.0 header, set model fix expires date.
        response.setDateHeader(HttpHeaders.EXPIRES, System.currentTimeMillis() + expiresSeconds * 1000);
        // Http 1.1 header, set model time after now.
        response.setHeader(HttpHeaders.CACHE_CONTROL, "private, max-age=" + expiresSeconds);
    }

    /**
     * 设置禁止客户端缓存的Header.
     *
     * @param response
     */
    public static void setNoCacheHeader(HttpServletResponse response) {
        // Http 1.0 header
        response.setDateHeader(HttpHeaders.EXPIRES, 1L);
        response.addHeader(HttpHeaders.PRAGMA, "no-cache");
        // Http 1.1 header
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0");
    }

    /**
     * 设置LastModified Header.
     *
     * @param response
     * @param lastModifiedDate
     */
    public static void setLastModifiedHeader(HttpServletResponse response, long lastModifiedDate) {
        response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModifiedDate);
    }

    /**
     * 设置Etag Header.
     *
     * @param response
     * @param etag
     */
    public static void setEtag(HttpServletResponse response, String etag) {
        response.setHeader(HttpHeaders.ETAG, etag);
    }

    /**
     * 根据浏览器If-Modified-Since Header, 计算文件是否已被修改.
     * <p>
     * 如果无修改, checkIfModify返回false ,设置304 not modify status.
     *
     * @param lastModified 内容的最后修改时间.
     */
    public static boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response,
                                               long lastModified) {
        long ifModifiedSince = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
        if ((ifModifiedSince != -1) && (lastModified < ifModifiedSince + 1000)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return false;
        }
        return true;
    }

    /**
     * 根据浏览器 If-None-Match Header, 计算Etag是否已无效.
     * <p>
     * 如果Etag有效, checkIfNoneMatch返回false, 设置304 not modify status.
     *
     * @param etag 内容的ETag.
     */
    public static boolean checkIfNoneMatchEtag(HttpServletRequest request, HttpServletResponse response, String etag) {
        String headerValue = request.getHeader(HttpHeaders.IF_NONE_MATCH);
        if (headerValue != null) {
            boolean conditionSatisfied = false;
            if (!"*".equals(headerValue)) {
                StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");

                while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
                    String currentToken = commaTokenizer.nextToken();
                    if (currentToken.trim().equals(etag)) {
                        conditionSatisfied = true;
                    }
                }
            } else {
                conditionSatisfied = true;
            }

            if (conditionSatisfied) {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                response.setHeader(HttpHeaders.ETAG, etag);
                return false;
            }
        }
        return true;
    }

    /**
     * 设置让浏览器弹出下载对话框的Header.
     *
     * @param fileName 下载后的文件名.
     */
    public static void setFileDownloadHeader(HttpServletResponse response, String fileName) {
        try {
            // 中文文件名支持
            String encodedfileName = new String(fileName.getBytes(), "ISO8859-1");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedfileName + "\"");
        } catch (UnsupportedEncodingException e) {
            e.getMessage();
        }
    }

    /**
     * 从request中获得参数，并返回可读的Map
     * application/x-www-form-urlencode
     * application/json
     * application/json;charset=UTF-8
     *
     * @param request
     * @return
     */
    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        String contentType = request.getHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE);
        Map<String, String> returnMap = new HashMap();
        if (MediaType.APPLICATION_JSON.equals(contentType) || MediaType.APPLICATION_JSON_UTF8.equals(contentType)) {
            // json类型参数
            String body = getBodyString(request);
            if (StringUtils.isNotBlank(body)) {
                try {
                    returnMap = JSONObject.parseObject(body, Map.class);
                } catch (Exception e) {

                }
            }
        } else {
            // 普通表单形式
            Map properties = request.getParameterMap();
            // 返回值Map
            Iterator entries = properties.entrySet().iterator();
            Entry entry;
            String name = "";
            String value = "";
            while (entries.hasNext()) {
                entry = (Entry) entries.next();
                name = (String) entry.getKey();
                Object valueObj = entry.getValue();
                if (null == valueObj) {
                    value = "";
                } else if (valueObj instanceof String[]) {
                    String[] values = (String[]) valueObj;
                    for (int i = 0; i < values.length; i++) {
                        value = values[i] + ",";
                    }
                    value = value.substring(0, value.length() - 1);
                } else {
                    value = valueObj.toString();
                }
                returnMap.put(name, value);
            }
        }
        // 参数Map
        return returnMap;
    }


    /**
     * 客户端对Http Basic验证的 Header进行编码.
     *
     * @param userName
     * @param password
     * @return
     */
    public static String encodeHttpBasic(String userName, String password) {
        String encode = userName + ":" + password;
        return "Basic " + Base64.encodeBase64String(encode.getBytes());
    }

    /**
     * 是否是Ajax异步请求
     *
     * @param request
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        return (request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString())) || (request.getHeader("Content-Type") != null && request.getHeader("Content-Type").startsWith("application/json"));
    }

    /**
     * 获取本地Ip4地址
     *
     * @return
     */
    public static final String getLocalIpAddress() {
        String ipString = "";
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address && !ip.getHostAddress().equals("127.0.0.1")) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {

        }
        return ipString;
    }

    /**
     * 判断访问URI是否是静态文件请求
     *
     * @throws Exception
     */
    public static boolean isStaticFile(String uri) {
        return StringUtils.endsWithAny(uri, staticFiles) && !StringUtils.endsWithAny(uri, new String[]{urlSuffix})
                && !StringUtils.endsWithAny(uri, new String[]{".jsp"}) && !StringUtils.endsWithAny(uri, new String[]{".java"});
    }

    /**
     * 客户端返回JSON字符串
     *
     * @param response
     * @param object
     * @return
     */
    public static void writeJson(HttpServletResponse response, Object object) {
        writeJson(response, JSON.toJSONString(object), "application/json;charset=UTF-8");
    }

    /**
     * 客户端返回字符串
     *
     * @param response
     * @param outputStr
     * @param contentType
     */
    public static void writeJson(HttpServletResponse response, String outputStr, String contentType) {
        try {
            // CORS setting
            response.setHeader("OpenAccess-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Origin", "*");
//            response.setHeader("Content-type", "application/json;charset=UTF-8");

            response.setContentType(contentType);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(outputStr);
            response.getWriter().flush();
            response.getWriter().close();

//            response.getWriter().write(string);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 输出Excel的头信息
     *
     * @param response
     * @param fileName
     */
    public static void writeExcel(HttpServletResponse response, String fileName) {
        try {
            response.setContentType("application/msexcel");
            response.setHeader("Content-Disposition", "attachment;filename=" + StrUtil.utf8Bytes(fileName));
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取完整路径
     *
     * @param request
     * @return
     */
    public static String getServerUrl(HttpServletRequest request) {
        String url = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + request.getContextPath();
        return url;
    }

    /**
     * @param request
     * @return
     */
    public static String getContextPath(HttpServletRequest request) {
        return request.getContextPath();
    }

    /**
     * @param request
     * @return
     */
    public static Map<String, String> getHttpHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        if (request != null) {
            Enumeration<String> enumeration = request.getHeaderNames();
            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    String key = enumeration.nextElement();
                    String value = request.getHeader(key);
                    map.put(key, value);
                }
            }
        }

        return map;
    }

    /**
     * 获取request对象
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return null;
        } else {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }
    }

    /**
     * 获取根路径
     *
     * @return
     */
    public static String getRootUrl() {
        HttpServletRequest request = getRequest();
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    /**
     * 获取客户端IP
     *
     * @return
     */
    public static String getClientIp() {
        HttpServletRequest request = getRequest();
        return getIpAddr(request);
    }

    /**
     * 获取request里面的参数
     *
     * @param name
     * @return
     */
    public static String getParameter(String name) {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getParameter(name);
        } else {
            return null;
        }
    }

    /**
     * 获取request里面的参数
     *
     * @param name
     * @return
     */
    public static String[] getParameterValues(String name) {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getParameterValues(name);
        } else {
            return null;
        }
    }

    /**
     * 获取Http header的参数
     *
     * @param name
     * @return
     */
    public static String getHeaderParam(String name) {
        HttpServletRequest request = getRequest();
        if (request != null) {
            return request.getHeader(name);
        } else {
            return null;
        }
    }

    /**
     * 获取request里面的参数
     *
     * @param request
     * @param handlerMethod
     * @return
     */
    public static Map<String, Object> getRequestMethodParams(HttpServletRequest request, HandlerMethod handlerMethod) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        List<Map<String, Object>> formParameters = new ArrayList<Map<String, Object>>();
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        if (methodParameters != null && methodParameters.length > 0) {
            for (MethodParameter methodParameter : methodParameters) {
                String paramName = methodParameter.getParameterName();
                Class<?> paramType = methodParameter.getParameterType();
                Object paramValue = null;
                if (!pathVariables.containsKey(paramName) && paramType != HttpServletRequest.class && paramType != HttpSession.class && paramType != Model.class) {

                    if (paramType.isArray()) {
                        List<String> paramList = new ArrayList<String>();
                        String[] params = request.getParameterValues(paramName);
                        if (params != null && params.length > 0) {
                            for (String param : params) {
                                paramList.add(param);
                            }
                        }
                        paramValue = paramList;
                    } else {
                        paramValue = (String) request.getParameter(paramName);
                        if ("password".equals(paramName) || "pwd".equals(paramName) || "passwd".equals(paramName) && paramValue != null && StringUtils.isNoneBlank(paramValue.toString())) {
                            paramValue = StringUtils.repeat("*", paramValue.toString().length());
                        }
                    }

                    Map<String, Object> formParam = new HashMap<String, Object>();
                    formParam.put(paramName, paramValue);
                    formParameters.add(formParam);
                }
            }
        }
        paramMap.put("pathVariables", pathVariables);
        paramMap.put("formParameters", formParameters);
        return paramMap;
    }

    /**
     * 通用请求格式转换，自动转换Json和Form请求到Map里面
     *
     * @param httpServletRequest
     * @return
     */
    public static Map<String, String> getHttpRequestParam(HttpServletRequest httpServletRequest) {
        Map<String, String> params = new HashMap<>();
        try {
            Map<String, String[]> requestParams = httpServletRequest.getParameterMap();
            if (requestParams != null && !requestParams.isEmpty()) {
                requestParams.forEach((key, value) -> params.put(key, value[0]));
            } else {
                StringBuilder paramSb = new StringBuilder();
                try {
                    String str = "";
                    BufferedReader br = httpServletRequest.getReader();
                    while ((str = br.readLine()) != null) {
                        paramSb.append(str);
                    }
                } catch (Exception e) {
                    System.out.println("httpServletRequest get requestbody error, cause : " + e);
                }
                if (paramSb.length() > 0) {
                    JSONObject paramJsonObject = JSON.parseObject(paramSb.toString());
                    if (paramJsonObject != null && !paramJsonObject.isEmpty()) {
                        paramJsonObject.forEach((key, value) -> params.put(key, String.valueOf(value)));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("commonHttpRequestParamConvert error, cause : " + e);
        }
        return params;
    }

    /**
     * 301永久跳转到指定URL，URL默认为相对路径
     *
     * @param url
     * @return
     */
    public static ModelAndView redirect301(String url) {
        RedirectView red = new RedirectView(url, true);
        red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return new ModelAndView(red);
    }

    /**
     * 302临时跳转
     *
     * @param url
     * @return
     */
    public static ModelAndView redirect302(String url) {
        RedirectView red = new RedirectView(url, true);
        red.setStatusCode(HttpStatus.FOUND);
        return new ModelAndView(red);
    }

}
