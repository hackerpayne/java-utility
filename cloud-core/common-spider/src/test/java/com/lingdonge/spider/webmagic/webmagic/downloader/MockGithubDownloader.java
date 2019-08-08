package com.lingdonge.spider.webmagic.webmagic.downloader;

import com.lingdonge.spider.webmagic.Page;
import com.lingdonge.spider.webmagic.Request;
import com.lingdonge.spider.webmagic.Task;
import com.lingdonge.spider.webmagic.downloader.Downloader;
import com.lingdonge.spider.webmagic.selector.PlainText;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author code4crafter@gmail.com
 */
public class MockGithubDownloader implements Downloader {

    @Override
    public Page download(Request request, Task task) {
        Page page = new Page();
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/html/mock-github.html");
        try {
            page.setRawText(IOUtils.toString(resourceAsStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        page.setRequest(new Request("https://github.com/code4craft/spider"));
        page.setUrl(new PlainText("https://github.com/code4craft/spider"));
        return page;
    }


    @Override
    public Page downloadHeader(Request request, Task task) {
       return download(request,task);
    }

    @Override
    public void setThread(int threadNum) {
    }
}
