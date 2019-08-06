package com.lingdonge.spring.http;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 短信发送工具
 *
 */
@Component
public class SmsSendUtil {

    @Resource
    RestTemplate restTemplate;

    /**
     * @param host
     * @param postParameters
     * @return
     */
    public String sendSmsByPost(String host, Map<String, Object> postParameters) {
        try {
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<Map<String, Object>>(postParameters, headers);
            return restTemplate.postForObject(host, httpEntity, String.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
