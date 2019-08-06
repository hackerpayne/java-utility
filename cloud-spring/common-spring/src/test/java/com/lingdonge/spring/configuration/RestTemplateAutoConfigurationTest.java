package com.lingdonge.spring.configuration;

import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

public class RestTemplateAutoConfigurationTest {


    /**
     * AsyncRestTemplate配合Guava的线程池，实现非常异步且简单的远程调用
     */
    public void testWithGuavaThreads() {
        AsyncRestTemplate template = new AsyncRestTemplate();

        //调用完后立即返回（没有阻塞）
        ListenableFuture<ResponseEntity<String>> future = template.getForEntity("http://localhost:9080/spring4/api", String.class);

        //设置异步回调
        future.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
            @Override
            public void onSuccess(ResponseEntity<String> result) {
                System.out.println("======client get result : " + result.getBody());
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("======client failure : " + t);
            }
        });


        System.out.println("==no wait");
    }
}
