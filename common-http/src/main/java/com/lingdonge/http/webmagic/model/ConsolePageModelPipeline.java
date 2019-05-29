package com.lingdonge.http.webmagic.model;

import com.lingdonge.http.webmagic.Task;
import com.lingdonge.http.webmagic.pipeline.PageModelPipeline;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Print page bean in console.<br>
 * Usually used in multithreading.<br>
 */
public class ConsolePageModelPipeline implements PageModelPipeline {
    @Override
    public void process(Object o, Task task) {
        System.out.println(ToStringBuilder.reflectionToString(o));
    }
}
