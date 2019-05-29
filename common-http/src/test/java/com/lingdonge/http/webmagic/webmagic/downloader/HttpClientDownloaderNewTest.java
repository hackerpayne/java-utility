package com.lingdonge.http.webmagic.webmagic.downloader;

import com.lingdonge.http.webmagic.Page;
import com.lingdonge.http.webmagic.Request;
import com.lingdonge.http.webmagic.Site;
import com.lingdonge.http.webmagic.Task;
import com.lingdonge.http.webmagic.downloader.HttpClientDownloaderNew;
import com.lingdonge.http.webmagic.proxy.SimpleProxyProvider;
import com.lingdonge.http.webmagic.selector.Html;
import com.lingdonge.http.webmagic.selector.Json;
import com.lingdonge.http.webmagic.selector.Selectable;
import com.kyle.utility.dates.TimeCounter;
import com.kyle.utility.http.DownloadUtil;
import com.kyle.utility.http.HttpConstant;
import com.kyle.utility.http.UserAgentUtil;
import com.kyle.utility.model.ModelProxy;
import com.kyle.utility.util.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.testng.annotations.Test;

import java.util.List;

public class HttpClientDownloaderNewTest {
    HttpClientDownloaderNew httpClientDownloader = new HttpClientDownloaderNew();

    public void testHttpClient() {
        Html html = httpClientDownloader.download("https://www.toutiao.com/a6533848835536454157/");

//        System.out.println(HtmlUtil.d);
    }

    @Test
    public void tempGet() {
        Task task = Site.me().setDomain("localhost").setCycleRetryTimes(5).toTask();
        Request request = new Request("https://www.toutiao.com/a6533848835536454157/");
//        request.addCookie("tt_webid","6498096636076738062");
//        request.addCookie("UM_distinctid","1614f2f5e55306-0544b95f63d01e-49526a-13c680-1614f2f5e56c5");
//        request.addCookie("__tasessionId","0q7mj91uw1523515384769");
//        request.addHeader("upgrade-insecure-requests","1");
        request.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:53.0) Gecko/20100101 Firefox/53.0");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        request.addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
        request.addHeader("Accept-Encoding", "gzip, deflate, br");
        Page page = httpClientDownloader.download(request, task);

        System.out.println(page.getRawText());

    }

    @Test
    public void downloadPageeTest() {

        Task task = Site.me().setDomain("localhost")
                .setUserAgent(UserAgentUtil.radomUserAgent())
//                .addCookie("ok","seo")
//                .addCookie("ok1","seo2")
                .setCycleRetryTimes(5).toTask();


        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new ModelProxy("http-dyn.abuyun.com", 9020, "H40X190LQ456VH8D", "915183ABA73B5F89")));

        Request request;
        Json json;
        Json jsonTemp;

        String productId = "1720704";

        Integer MAXTRY = 3;
        for (Integer pageCur = 1; pageCur <= 50; pageCur++) {
            request = new Request(StringUtils.format("http://club.jd.com/discussion/getProductPageImageCommentList.action?productId={}&isShadowSku=0&page={}&pageSize=10&_=1524124843628", productId, pageCur));

            int redo = 0;     //重试次数
            while (redo < MAXTRY + 1) {     //MAXTRY为最大重试次数
                try {

                    Page page = httpClientDownloader.download(request, task);

                    json = new Json(page.getRawText());

                    List<Selectable> listItems = json.jsonPath("$.imgComments.imgList").nodes();
                    for (Selectable item : listItems) {
                        jsonTemp = new Json(item.get());

                        String imgPath = jsonTemp.jsonPath("$.imageUrl").get();
                        if (imgPath.startsWith("//"))
                            imgPath = "http:" + imgPath;
                        String id = jsonTemp.jsonPath("$.imageId").get();

                        String newFile = id + "." + FilenameUtils.getExtension(imgPath);
                        try {
                            DownloadUtil.download(imgPath, newFile, "/Users/kyle/Downloads/images-download/" + productId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    break;                    //执行成功后直接退出此循环
                } catch (Exception ex) {
                    redo++;                 //异常时,重试次数增加
                    continue;             //结束本次循环
                }
            }


        }


    }


    @Test
    public void downloadPage() {

        Task task = Site.me().setDomain("localhost")
                .setUserAgent(UserAgentUtil.radomUserAgent())
//                .addCookie("ok","seo")
//                .addCookie("ok1","seo2")
                .setCycleRetryTimes(5).toTask();
        Request request = new Request("http://myip.ipip.net/");

        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new ModelProxy("http-dyn.abuyun.com", 9020, "H918MGT13685WS9D", "9B1B227F50906B73")));

        for (int i = 1; i <= 10; i++) {
            Page page = httpClientDownloader.download(request, task);

//        System.out.println("当前链接：");
//        System.out.println(page.getLocationUrl());
            System.out.println("页面内容：");
            System.out.println(page.getRawText());
        }
    }

    @Test
    public void getPage() {


        // 网易考拉
        Task task = Site.me().setDomain("localhost")
                .setCycleRetryTimes(5)
                .setUserAgent("Dalvik/1.6.0 (Linux; U; Android 4.4.2; vivo X20 Build/NMF26X)")
                .setUseGzip(true)
                .addCookie("current_env", "online").toTask();

        Request request = new Request("https://sp.kaola.com/api/comment?new&goodsId=6832&tagType=100&pageNo=1&tagName=%E5%85%A8%E9%83%A8&pageSize=30");
        request.addHeader("apiVersion", "207");
        request.addHeader("platform", "1");
        request.addHeader("appVersion", "3.8.5");
        request.addHeader("deviceModel", "vivo X20");
        request.addHeader("deviceUdID", "a4dfdaaf909c3e0f30833afa710cdc91f1aeb414");
        request.addHeader("appChannel", "19");
        request.addHeader("appSystemVersion", "4.4.2");
        request.addHeader("version", "30080501");

        TimeCounter.markStart();

        Page page = httpClientDownloader.download(request, task);

        TimeCounter.markEnd();
        System.out.println(page.getUrl());
        System.out.println(page.getLocationUrl());

//        System.out.println(page);
    }

    @Test
    public void testYmatou() {
        // 洋码头
        Task task = Site.me().setDomain("localhost")
                .setCycleRetryTimes(5)
                .setUserAgent("okhttp/3.9.0")
                .setUseGzip(true)
                .addCookie("current_env", "online").toTask();

        Request request = new Request("https://app.ymatou.com/api/prodextra/evaluation?evaluationId=&filter=&pageIndex=1&productId=feaf91a3-69b7-4bb7-a931-abd35272805e");
        request.addHeader("sign", "995ddaa133613054ca87bb9b4889f430");
//        request.addHeader("timestamp", String.valueOf(System.currentTimeMillis()));
        request.addHeader("timestamp", "1521436565075");
        request.addHeader("app-key", "android");
        request.addHeader("accept-version", "2.0.0");
        request.addHeader("cookieid", "ffffffff-9f0d-986b-297b-b11171c7481b");
        request.addHeader("ymt-pars", "os=4.4.2&machinename=vivo X20&client=android&imei=354730010002842&yid=e13be1b0-0c86-4b31-b4f5-ad22a9e53d53&format=json&requestid=ffffffff-9f0d-986b-297b-b11171c7481b1521436565083&type=buyer&channel=anzhi&network=wifi");
        request.addHeader("deviceid", "ffffffff-9f0d-986b-297b-b11171c7481b");
        request.addHeader("app-version", "5.0.0");

        TimeCounter.markStart();

        Page page = httpClientDownloader.download(request, task);

        TimeCounter.markEnd();
        System.out.println(page.getUrl());
        System.out.println(page.getLocationUrl());
    }

    @Test
    public void headPage() {
        Task task = Site.me().setDomain("localhost").setCycleRetryTimes(5).toTask();
        Request request = new Request("https://www.toutiao.com/a6533848835536454157/");
        request.setMethod(HttpConstant.Method.HEAD);

//        TimeCounter.markStart();
//        Page page = httpClientDownloader.download(request, task);
//        TimeCounter.markEnd();
//        System.out.println(page.getUrl());
//        System.out.println(page.getLocationUrl());

        TimeCounter.markStart();
        Page page2 = httpClientDownloader.downloadHeader(request, task);
        TimeCounter.markEnd();

        System.out.println(page2.getUrl());
        System.out.println(page2.getLocationUrl());


//        System.out.println(page);
    }

}