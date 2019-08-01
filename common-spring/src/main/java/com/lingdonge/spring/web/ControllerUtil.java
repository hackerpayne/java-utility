package com.lingdonge.spring.web;

import com.google.common.collect.Lists;
import com.lingdonge.spring.bean.request.RequestMethodItem;
import com.lingdonge.spring.bean.request.RequestMethodParameter;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

/**
 * 控制器相关操作类，比如扫描所有路由等
 */
public class ControllerUtil {

    private static LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 获取所有URL列表
     *
     * @return
     */
    public static List<String> getAllUrl(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        List<String> urlList = new ArrayList<>();
        for (RequestMappingInfo info : map.keySet()) { //获取url的Set集合，一个方法可能对应多个url
            Set<String> patterns = info.getPatternsCondition().getPatterns();
            urlList.addAll(patterns);
        }
        return urlList;
    }


    public static List<Map<String, String>> getAllUrlListMap(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();

        // 打印出更详细的内容
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            Map<String, String> map1 = new HashMap<String, String>();
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            PatternsRequestCondition p = info.getPatternsCondition();
            for (String url : p.getPatterns()) {
                map1.put("url", url);
            }
            map1.put("className", method.getMethod().getDeclaringClass().getName()); // 类名
            map1.put("method", method.getMethod().getName()); // 方法名
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                map1.put("type", requestMethod.toString());
            }
            list.add(map1);
        }
        return list;
    }


    /**
     * 从类里面解析所有URL
     *
     * @param requestMappingHandlerMapping
     * @return
     */
    public static List<RequestMethodItem> getAllUrls(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        List<RequestMethodItem> listRequestItems = Lists.newArrayList();

        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            RequestMappingInfo requestMappingInfo = m.getKey(); // 获取URL列表
            HandlerMethod handlerMethod = m.getValue(); // Controller的处理方法

            // 请求路径
            String path = requestMappingInfo.getPatternsCondition().toString();
            path = path.replace("[", "").replace("]", "");

            // 请求方法
            String requestMethod = requestMappingInfo.getMethodsCondition().toString();
            requestMethod = requestMethod.replace("[", "").replace("]", "");

            // 返回header类型
            String responseType = requestMappingInfo.getProducesCondition().toString();
            responseType = responseType.replace("[", "").replace("]", "");

            // 参数
            MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
            List<RequestMethodParameter> parameters = Lists.newArrayListWithExpectedSize(methodParameters.length);
            for (MethodParameter methodParameter : methodParameters) {
                // 参数名称
                // 如果没有discover参数会是null.参考 LocalVariableTableParameterNameDiscoverer
                methodParameter.initParameterNameDiscovery(discoverer);
                String parameterName = methodParameter.getParameterName();

                // 参数类型
                Class<?> parameterType = methodParameter.getParameterType();

                // 参数注解
                Object[] parameterAnnotations = methodParameter.getParameterAnnotations();

                // 注解
                String annoation = Arrays.toString(parameterAnnotations);

                RequestMethodParameter parameter = new RequestMethodParameter();
                parameter.setAnnoation(annoation);
                parameter.setName(parameterName);
                parameter.setType(parameterType.toString());
                parameters.add(parameter);
            }

            // 可以获取自定 注解里面的内容做处理
            ApiOperation documentAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);

            String handleMethodStr = handlerMethod.toString();

            // 解析Controller路径，解析结果为：com.lingdonge.codegen.controller.TestController
            String controllerStr = handleMethodStr.split("\\(")[0];
            controllerStr = controllerStr.split(" ")[2];
            int i = controllerStr.lastIndexOf(".");
            controllerStr = controllerStr.substring(0, i);

            // 解析完整的方法：解析结果为：com.lingdonge.codegen.controller.TestController.test2
            String methodStr = handleMethodStr.split(" ")[2];

            RequestMethodItem item = new RequestMethodItem();
            item.setPath(path);
            item.setRequestMethod(requestMethod);
            item.setParameters(parameters);
            item.setMethod(handlerMethod.getMethod().getName());
            item.setClassName(handlerMethod.getMethod().getDeclaringClass().getName());//类名
            item.setController(controllerStr);
            item.setMethodFull(handlerMethod.toString());
            item.setApiOperation(documentAnnotation);
            item.setResponseType(responseType);

            listRequestItems.add(item);
        }

        Collections.sort(listRequestItems);

        return listRequestItems;
    }


}
