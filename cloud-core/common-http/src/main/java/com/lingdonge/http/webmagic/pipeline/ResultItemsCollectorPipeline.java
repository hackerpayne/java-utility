package com.lingdonge.http.webmagic.pipeline;

import com.lingdonge.http.webmagic.ResultItems;
import com.lingdonge.http.webmagic.Task;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ResultItemsCollectorPipeline implements CollectorPipeline<ResultItems> {

    private List<ResultItems> collector = new ArrayList<ResultItems>();

    @Override
    public synchronized void process(ResultItems resultItems, Task task) {
        collector.add(resultItems);
    }

    @Override
    public List<ResultItems> getCollected() {
        return collector;
    }
}
