package com.lingdonge.http.webmagic.webmagic.example;

import com.lingdonge.http.webmagic.ResultItems;
import com.lingdonge.http.webmagic.Spider;
import com.lingdonge.http.webmagic.Task;
import com.lingdonge.http.webmagic.pipeline.Pipeline;
import com.lingdonge.http.webmagic.processor.example.GithubRepoPageProcessor;
import com.lingdonge.http.webmagic.webmagic.downloader.MockGithubDownloader;
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
