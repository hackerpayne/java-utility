package com.lingdonge.spider.webmagic.configurable;

import com.lingdonge.spider.webmagic.Page;
import com.lingdonge.spider.webmagic.Site;
import com.lingdonge.spider.webmagic.processor.PageProcessor;
import com.lingdonge.spider.webmagic.utils.Experimental;

import java.util.List;

/**
 */
@Experimental
public class ConfigurablePageProcessor implements PageProcessor {

    private Site site;

    private List<ExtractRule> extractRules;

    public ConfigurablePageProcessor(Site site, List<ExtractRule> extractRules) {
        this.site = site;
        this.extractRules = extractRules;
    }

    @Override
    public void process(Page page) {
        for (ExtractRule extractRule : extractRules) {
            if (extractRule.isMulti()) {
                List<String> results = page.getHtml().selectDocumentForList(extractRule.getSelector());
                if (extractRule.isNotNull() && results.size() == 0) {
                    page.setSkip(true);
                } else {
                    page.getResultItems().put(extractRule.getFieldName(), results);
                }
            } else {
                String result = page.getHtml().selectDocument(extractRule.getSelector());
                if (extractRule.isNotNull() && result == null) {
                    page.setSkip(true);
                } else {
                    page.getResultItems().put(extractRule.getFieldName(), result);
                }
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

}
