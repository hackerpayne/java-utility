package com.lingdonge.spring.http;

import com.google.common.collect.Maps;
import com.lingdonge.spring.annotation.EnableKyleRestTemplate;
import com.lingdonge.spring.restful.ModelTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableKyleRestTemplate
@EnableEurekaClient //开启服务注册
@EnableDiscoveryClient // 开启服务发现
@Slf4j
public class RestTemplateUtilTest {

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private RestTemplateUtil restTemplateUtil;

    @Test
    public void postMap() {
        Map<String, Object> mapArticle = Maps.newHashMap();
        mapArticle.put("TITLE", "这是一个好的标题，不");
        mapArticle.put("CONTENT", "这是一下不好的内容，这非常非常，不好。不合");

        String result = restTemplateUtil.postJsonMap("http://10.15.2.202:9534/sentimentAnalysis", mapArticle);
//        String result = restTemplateUtil.postMap("http://WI-NEGATIVE-ANALYSIS-SERVICE/sentimentAnalysis", mapArticle);

        log.info("匹配结果：" + result);
    }

    @Test
    public void testGetHtml() {
        String html = restTemplateUtil.getHtml("http://www.baidu.com");
        log.info("testGetHtml获取HTML：{}", html);
    }

    @RequestMapping("/gethello")
    public String getHello() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class);
        String body = responseEntity.getBody();
        HttpStatus statusCode = responseEntity.getStatusCode();
        int statusCodeValue = responseEntity.getStatusCodeValue();
        HttpHeaders headers = responseEntity.getHeaders();
        StringBuffer result = new StringBuffer();
        result.append("responseEntity.getBody()：").append(body).append("<hr>")
                .append("responseEntity.getStatusCode()：").append(statusCode).append("<hr>")
                .append("responseEntity.getStatusCodeValue()：").append(statusCodeValue).append("<hr>")
                .append("responseEntity.getHeaders()：").append(headers).append("<hr>");
        return result.toString();
    }

    @RequestMapping("/sayhello")
    public String sayHello() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE/sayhello?name={1}", String.class, "张三");
        return responseEntity.getBody();
    }

    @RequestMapping("/sayhello2")
    public String sayHello2() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "李四");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE/sayhello?name={name}", String.class, map);
        return responseEntity.getBody();
    }

    @RequestMapping("/book3")
    public ModelTest book3() {
        ModelTest book = new ModelTest();
        book.setValue("红楼梦");
        ResponseEntity<ModelTest> responseEntity = restTemplate.postForEntity("http://HELLO-SERVICE/getbook2", book, ModelTest.class);
        return responseEntity.getBody();
    }

    @RequestMapping("/put")
    public void put() {
        ModelTest book = new ModelTest();
        book.setTitle("红楼梦");
        restTemplate.put("http://HELLO-SERVICE/getbook3/{1}", book, 99);
    }

    @RequestMapping("/delete")
    public void delete() {
        restTemplate.delete("http://HELLO-SERVICE/getbook4/{1}", 100);
    }


}
