package com.lingdonge.spring.http;

import com.alibaba.fastjson.JSON;
import com.lingdonge.spring.enums.ContentTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RestTemplate的进一步简化封装
 * 使用MultiValueMap用来放参数，（使用HashMap不行,具体原因可见http://www.cnblogs.com/shoren/p/RestTemplate-problem.html ），
 * Map的value必须为字符串，直接用Integer数值会报错，原因是未设置 HttpMessageConverter 进行转换
 * https://stackoverflow.com/questions/20479074/spring-restremplate-postforobject-with-request-parameter-having-integer-value
 */
@Slf4j
public class RestTemplateUtil {

    /**
     * 非Spring环境时自动注入新的对象
     *
     * @return
     */
    public RestTemplate getRestTemplate() {
        if (null == restTemplate) {

            log.info("RestTemplate对象不存在，将会使用默认条件创建");

            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setReadTimeout(5000);
            requestFactory.setConnectTimeout(5000);

            // 添加转换器
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
            messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
            messageConverters.add(new FormHttpMessageConverter());
            messageConverters.add(new MappingJackson2HttpMessageConverter());

            restTemplate = new RestTemplate(messageConverters);
            restTemplate.setRequestFactory(requestFactory);
            restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        }
        return restTemplate;
    }

    /**
     * 注入Spring的配置
     *
     * @param restTemplate
     */
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private RestTemplate restTemplate;

    public RestTemplateUtil() {

    }

    public RestTemplateUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 判断接口返回是否为200
     *
     * @param url
     * @return
     */
    public Boolean ping(String url) {
        try {
            ResponseEntity<String> responseEntity = getRestTemplate().getForEntity(url, String.class);
            HttpStatus status = responseEntity.getStatusCode();//获取返回状态
            return status.is2xxSuccessful();//判断状态码是否为2开头的
        } catch (Exception e) {
            return false; //502 ,500是不能正常返回结果的，需要catch住，返回一个false
        }
    }

    /**
     * 根据ContentType生成不同类型对应的Header信息
     *
     * @param contentTypeEnum
     * @return
     */
    public HttpHeaders getHeaders(ContentTypeEnum contentTypeEnum) {
        HttpHeaders headers = new HttpHeaders();

        switch (contentTypeEnum) {
            case XML:
//                headers.add("Accept", ContentTypeEnum.XML.getValue());
                headers.add("Content-Type", ContentTypeEnum.XML.getValue() + "; charset=UTF-8");
                break;
            case FORM:
//                headers.add("Accept", ContentTypeEnum.FORM.getValue());
                headers.add("Content-Type", ContentTypeEnum.FORM.getValue() + "; charset=UTF-8");
//                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                break;
            case JSON:
//                headers.add("Accept", ContentTypeEnum.JSON.getValue());
//                headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);

//                headers.add("Content-Type", ContentTypeEnum.JSON.getValue() + "; charset=UTF-8");
                headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

                // 或者使用：
//                MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
//                headers.setContentType(type);
//                headers.add("Accept", MediaType.APPLICATION_JSON.toString());

                // 2、Json提交
//        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
//                headers.add("Accept", MediaType.ALL_VALUE);
//                headers.add("Accept", MediaType.TEXT_PLAIN_VALUE);
//                headers.add("Accept", MediaType.TEXT_HTML_VALUE);
//                headers.add("Accept", MediaType.APPLICATION_JSON.toString());

//                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                // 可以解决Post中文乱码的问题，最好是放到构造RestTemplate中统一处理
//        MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
//        headers.setContentType(type);
                break;
            case MULTIPART:
//                headers.add("Accept", ContentTypeEnum.MULTIPART.getValue());
                headers.add("Content-Type", ContentTypeEnum.MULTIPART.getValue() + "; charset=UTF-8");
                break;
            default:
                break;
        }
        headers.add("Accpet-Encoding", "gzip");
        headers.add("Content-Encoding", "UTF-8");
//        headers.add("Accept", "application/json");
//        headers.add("Content-Type", "application/json; charset=UTF-8");

        return headers;
    }

    /**
     * Get
     *
     * @param url
     * @return
     */
    public String get(String url) {
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, getHeaders(ContentTypeEnum.JSON));
        ResponseEntity<String> response = getRestTemplate().exchange(url, HttpMethod.GET, requestEntity, String.class);
        return response.getBody();
    }

    /**
     * Get获取HTML
     *
     * @param url
     * @return
     */
    public String getHtml(String url) {
        return getRestTemplate().getForObject(url, String.class);
    }

    /**
     * 带参数请求HTML
     *
     * @param url
     * @param mapQuery
     * @return
     */
    public String getHtml(String url, Map<String, Object> mapQuery) {
        return getRestTemplate().getForObject(url, String.class, mapQuery);
    }

    /**
     * Post请求
     *
     * @param url
     * @param data
     * @return
     */
    public String post(String url, String data) {
        HttpEntity<String> requestEntity = new HttpEntity<String>(data, getHeaders(ContentTypeEnum.JSON));
        return getRestTemplate().postForObject(url, requestEntity, String.class);
    }

    /**
     * 封装一下，使用Map提交。默认情况下不能使用HashMap
     *
     * @param url
     * @param param
     * @return
     */
    public String postMap(String url, Map<String, String> param) {
        return postMap(url, param, ContentTypeEnum.FORM);
    }

    /**
     * @param url
     * @param param
     * @param contentTypeEnum
     * @return
     */
    public String postMap(String url, Map<String, String> param, ContentTypeEnum contentTypeEnum) {
        MultiValueMap<String, Object> paramList = new LinkedMultiValueMap<String, Object>();//参数放入一个map中，restTemplate不能用hashMap

        for (Map.Entry<String, String> map : param.entrySet()) {
            paramList.add(map.getKey(), map.getValue());
        }
        return postMap(url, paramList, contentTypeEnum);
    }

    /**
     * 提交Map数据并返回字符串
     * 封装参数，千万不要替换为Map与HashMap，否则参数无法传递
     *
     * @param url
     * @param param
     * @param contentTypeEnum
     * @return
     */
    public String postMap(String url, MultiValueMap<String, Object> param, ContentTypeEnum contentTypeEnum) {

//         设置请求数据和Header
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(param, getHeaders(contentTypeEnum));
        ResponseEntity<String> response = getRestTemplate().exchange(url, HttpMethod.POST, requestEntity, String.class);
        return response.getBody();
//        return restTemplate.postForObject(url,param,String.class);
    }

    /**
     * POST JSON并返回字符串
     *
     * @param url
     * @param param
     * @return
     */
    public String postJsonMap(String url, Map<String, Object> param) {
        return postJsonObj(url, param);
    }

    /**
     * Post JSON并返回字符串
     *
     * @param url
     * @param param
     * @return
     */
    public String postJsonMapSimple(String url, Map<String, Object> param) {
        HttpHeaders headers = getHeaders(ContentTypeEnum.JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<Map<String, Object>>(param, headers);
        return getRestTemplate().postForObject(url, httpEntity, String.class);
    }

    /**
     * 提交Json并返回字符串
     *
     * @param url
     * @param entityClass
     * @return
     */
    public <T> String postJsonEntity(String url, Class<T> entityClass) {
        return postJsonObj(url, entityClass);
    }

    /**
     * Post Json 数据
     *
     * @param url
     * @param param
     */
    public String postBody(String url, Map<String, String> param) {
        return postBodyTo(url, param, String.class);
    }

    /**
     * POST 转 实体
     *
     * @param url
     * @param param
     * @param responseType
     * @param <T>
     * @return
     */
    public <T> T postBodyTo(String url, Map<String, String> param, Class<T> responseType) {
        HttpEntity<Map<String, String>> formEntity = new HttpEntity<Map<String, String>>(param, getHeaders(ContentTypeEnum.JSON));
        return getRestTemplate().postForObject(url, formEntity, responseType);
    }

    /**
     * 提交Json对象
     *
     * @param url
     * @param obj
     * @return
     */
    public String postJsonObj(String url, Object obj) {
        HttpEntity<String> formEntity = new HttpEntity<String>(JSON.toJSONString(obj), getHeaders(ContentTypeEnum.JSON));
        return getRestTemplate().postForObject(url, formEntity, String.class);// 请求指定的实体，并返回为相应的解析结果
    }


    /**
     * 直接POST纯文本的结果
     *
     * @param url
     * @param postData
     * @return
     */
    public String postPlainText(String url, String postData) {
        HttpEntity<String> formEntity = new HttpEntity<String>(postData, getHeaders(ContentTypeEnum.JSON));
        return getRestTemplate().postForObject(url, formEntity, String.class);// 请求指定的实体，并返回为相应的解析结果
    }

}
