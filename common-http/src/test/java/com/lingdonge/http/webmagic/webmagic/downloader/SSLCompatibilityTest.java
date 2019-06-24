package com.lingdonge.http.webmagic.webmagic.downloader;


import com.lingdonge.http.webmagic.Page;
import com.lingdonge.http.webmagic.Request;
import com.lingdonge.http.webmagic.Site;
import com.lingdonge.http.webmagic.Task;
import com.lingdonge.http.webmagic.downloader.HttpClientDownloader;
import org.junit.Test;

/**
 * @author code4crafter@gmail.com
 * Date: 2017/11/29
 * Time: 下午1:32
 */
public class SSLCompatibilityTest {

    @Test
    public void test_tls12() throws Exception {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Task task = Site.me().setCycleRetryTimes(5).toTask();
        Request request = new Request("https://juejin.im/");
        Page page = httpClientDownloader.download(request, task);
//        assertThat(page.isDownloadSuccess()).isTrue();
    }
}
