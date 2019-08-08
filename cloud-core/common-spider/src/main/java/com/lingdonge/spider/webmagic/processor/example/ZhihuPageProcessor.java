package com.lingdonge.spider.webmagic.processor.example;

import com.lingdonge.spider.webmagic.Page;
import com.lingdonge.spider.webmagic.Site;
import com.lingdonge.spider.webmagic.Spider;
import com.lingdonge.spider.webmagic.processor.PageProcessor;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.6.0
 */
public class ZhihuPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
        page.addTargetRequests(page.getHtml().links().regex("https://www\\.zhihu\\.com/question/\\d+/answer/\\d+.*").all());
        page.putField("title", page.getHtml().xpath("//h1[@class='QuestionHeader-title']/text()").toString());
        page.putField("question", page.getHtml().xpath("//div[@class='QuestionRichText']//tidyText()").toString());
        page.putField("answer", page.getHtml().xpath("//div[@class='QuestionAnswer-content']/tidyText()").toString());
        if (page.getResultItems().get("title")==null){
            //skip this page
            page.setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new ZhihuPageProcessor()).addUrl("https://www.zhihu.com/explore").run();
    }
}
