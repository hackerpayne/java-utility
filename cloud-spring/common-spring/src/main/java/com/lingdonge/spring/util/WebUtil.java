package com.lingdonge.spring.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lingdonge.core.http.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.ServerRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

@Slf4j
public class WebUtil extends org.springframework.web.util.WebUtils {

    /**
     * 获取 HttpServletRequest
     *
     * @return {HttpServletRequest}
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.warn("非web环境, 无法获取请求体.");
            return null;
        }
        return attributes.getRequest();
    }

    /**
     * 获取 HttpServletResponse
     *
     * @return {HttpServletResponse}
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            log.warn("非web环境, 无法获取响应对象.");
            return null;
        }
        return attributes.getResponse();
    }

    /**
     * 获取Session对象
     *
     * @return
     */
    public static HttpSession getSession() {
        HttpSession session = getRequest().getSession();
        return session;
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
     * 获取本地Ip4地址
     *
     * @return
     */
    public static final String getLocalIpAddress() throws SocketException {
        String ipString = "";
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

        return ipString;
    }

    /**
     * 写Html
     *
     * @param response
     * @param html
     * @throws IOException
     */
    public static void renderStr(HttpServletResponse response, String html) throws IOException {
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(html);
        response.getWriter().flush();
        response.getWriter().close();
    }

    /**
     * 客户端返回JSON字符串
     *
     * @param response HttpServletResponse
     * @param result   结果对象
     */
    public static void renderJson(HttpServletResponse response, Object result) throws IOException {
        renderJson(response, result, MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    /**
     * 返回json
     *
     * @param response    HttpServletResponse
     * @param result      结果对象
     * @param contentType contentType
     */
    public static void renderJson(HttpServletResponse response, Object result, String contentType) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(contentType);
        try (PrintWriter out = response.getWriter()) {
            out.append(JSON.toJSONString(result));
        }
    }

    /**
     * 从 request 从获取 body, 并转成 javabean, contentType 必须包含'application/json'
     *
     * @param request HttpServletRequest
     * @param clazz   返回结果类型
     * @param <T>     返回结果类型
     * @return
     * @throws IOException
     */
    public static <T> T renderJson(HttpServletRequest request, Class<T> clazz) throws IOException {
        String contentType = request.getContentType();
        MediaType mediaType = MediaType.valueOf(contentType);
        if (mediaType.includes(MediaType.APPLICATION_JSON)) {
            ServletInputStream inputStream = request.getInputStream();
            return JSON.parseObject(inputStream, clazz);
        }
        return null;
    }

    /**
     * 客户端返回字符串
     *
     * @param response
     * @param outputStr
     * @param contentType
     */
    public static void renderJson(HttpServletResponse response, String outputStr, String contentType) throws IOException {
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
    }

    /**
     * 输出Excel的头信息
     *
     * @param response
     * @param fileName
     */
    public static void renderExcel(HttpServletResponse response, String fileName) {
        response.setContentType("application/msexcel");
        response.setHeader("Content-Disposition", "attachment;filename=" + StrUtil.utf8Bytes(fileName));
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
    }

    /**
     * 设置让浏览器弹出下载对话框的Header
     *
     * @param response
     * @param fileName 指定下载的文件名
     */
    public static void renderDownload(HttpServletResponse response, String fileName) {
        try {
            // 中文文件名支持
            String encodedfileName = new String(fileName.getBytes(), "ISO8859-1");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedfileName + "\"");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取完整路径
     *
     * @param request
     * @return
     */
    public static String getRequestFullUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    /**
     * @param request
     * @return
     */
    public static String getContextPath(HttpServletRequest request) {
        return request.getContextPath();
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

//    /**
//     * 获取客户端IP
//     *
//     * @return
//     */
//    public static String getClientIp() {
//        HttpServletRequest request = getRequest();
//        return RequestUtil.getIpAddr(request);
//    }

    /**
     * 获取客户端ip. 请求体默认从全局上下文获取.
     *
     * @param otherHeaderNames
     * @return
     */
    public static String getClientIP(String... otherHeaderNames) {
        HttpServletRequest request = WebUtil.getRequest();
        if (request == null) {
            return null;
        }

        return getClientIP(request, otherHeaderNames);
    }

    /**
     * 获取客户端ip
     *
     * @param request          请求对象
     * @param otherHeaderNames 其他自定义头文件
     * @return
     */
    public static String getClientIP(HttpServletRequest request, String... otherHeaderNames) {
        return ServletUtil.getClientIP(request, otherHeaderNames);
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
     * 从request中获得参数，并返回可读的Map,自动转换Json和Form请求到Map里面
     * application/x-www-form-urlencode
     * application/json
     * application/json;charset=UTF-8
     *
     * @param request
     * @return
     */
    public static Map<String, String> getParameterMap(HttpServletRequest request) {
        String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
        Map<String, String> returnMap = new HashMap();

        if (MediaType.APPLICATION_JSON.equals(contentType) || MediaType.APPLICATION_JSON_UTF8.equals(contentType)) {  // json类型参数
            String body = RequestUtil.getBodyString(request);
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
            Map.Entry entry;
            String name = "";
            String value = "";
            while (entries.hasNext()) {
                entry = (Map.Entry) entries.next();
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
