package com.lingdonge.http.webmagic.handler;

import com.lingdonge.http.webmagic.Page;
import com.lingdonge.http.webmagic.Site;
import com.lingdonge.http.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class CompositePageProcessor implements PageProcessor {

    private Site site;

    private List<SubPageProcessor> subPageProcessors = new ArrayList<SubPageProcessor>();

    public CompositePageProcessor(Site site) {
        this.site = site;
    }

    @Override
    public void process(Page page) {
        for (SubPageProcessor subPageProcessor : subPageProcessors) {
            if (subPageProcessor.match(page.getRequest())) {
                MatchOther matchOtherProcessorProcessor = subPageProcessor.processPage(page);
                if (matchOtherProcessorProcessor == null || matchOtherProcessorProcessor != MatchOther.YES) {
                    return;
                }
            }
        }
    }

    public CompositePageProcessor setSite(Site site) {
        this.site = site;
        return this;
    }

    public CompositePageProcessor addSubPageProcessor(SubPageProcessor subPageProcessor) {
        this.subPageProcessors.add(subPageProcessor);
        return this;
    }

    public CompositePageProcessor setSubPageProcessors(SubPageProcessor... subPageProcessors) {
        this.subPageProcessors = new ArrayList<SubPageProcessor>();
        for (SubPageProcessor subPageProcessor : subPageProcessors) {
            this.subPageProcessors.add(subPageProcessor);
        }
        return this;
    }

    @Override
    public Site getSite() {
        return site;
    }
}
