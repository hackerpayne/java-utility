package com.lingdonge.spider.webmagic.webmagic.example;

import com.lingdonge.spider.webmagic.ResultItems;
import com.lingdonge.spider.webmagic.Spider;
import com.lingdonge.spider.webmagic.Task;
import com.lingdonge.spider.webmagic.pipeline.Pipeline;
import com.lingdonge.spider.webmagic.processor.example.GithubRepoPageProcessor;
import com.lingdonge.spider.webmagic.webmagic.downloader.MockGithubDownloader;
import org.junit.Test;

/**
 * @author code4crafter@gmail.com
 * Date: 16/1/19
 * Time: 上午7:27
 */
public class GithubRepoPageProcessorTest {

    @Test
    public void test_github() throws Exception {
        Spider.create(new GithubRepoPageProcessor()).addPipeline(new Pipeline() {
            @Override
            public void process(ResultItems resultItems, Task task) {
//                assertThat(((String) resultItems.get("name")).trim()).isEqualTo("spider");
//                assertThat(((String) resultItems.get("author")).trim()).isEqualTo("code4craft");
            }
        }).setDownloader(new MockGithubDownloader()).test("https://github.com/code4craft/spider");
    }
}
