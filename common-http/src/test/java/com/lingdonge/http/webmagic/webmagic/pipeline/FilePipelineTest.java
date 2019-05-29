package com.lingdonge.http.webmagic.webmagic.pipeline;


import com.lingdonge.http.webmagic.Request;
import com.lingdonge.http.webmagic.ResultItems;
import com.lingdonge.http.webmagic.Site;
import com.lingdonge.http.webmagic.Task;
import com.lingdonge.http.webmagic.pipeline.FilePipeline;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * Created by ywooer on 2014/5/6 0006.
 */
public class FilePipelineTest {

    private static ResultItems resultItems;
    private static Task task;

    @BeforeClass
    public static void before() {
        resultItems = new ResultItems();
        resultItems.put("content", "spider 爬虫工具");
        Request request = new Request("http://www.baidu.com");
        resultItems.setRequest(request);

        task = new Task() {
            @Override
            public String getUUID() {
                return UUID.randomUUID().toString();
            }

            @Override
            public Site getSite() {
                return null;
            }
        };
    }
    @Test
    public void testProcess() {
        FilePipeline filePipeline = new FilePipeline();
        filePipeline.process(resultItems, task);
    }
}
