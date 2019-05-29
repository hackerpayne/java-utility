package com.lingdonge.http.webmagic.pipeline;

import com.lingdonge.http.webmagic.ResultItems;
import com.lingdonge.http.webmagic.Task;

import java.util.Map;

/**
 * 保存到控制台显示
 */
public class ConsolePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        System.out.println("get page: " + resultItems.getRequest().getUrl());

        //遍历所有结果，输出到控制台，上面例子中的"author"、"name"、"readme"都是一个key，其结果则是对应的value

        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            System.out.println(entry.getKey() + ":\t" + entry.getValue());
        }
    }
}
