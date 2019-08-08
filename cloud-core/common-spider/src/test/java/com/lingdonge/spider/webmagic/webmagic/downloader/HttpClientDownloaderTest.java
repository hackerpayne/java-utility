package com.lingdonge.spider.webmagic.webmagic.downloader;

import com.lingdonge.spider.webmagic.Page;
import com.lingdonge.spider.webmagic.Request;
import com.lingdonge.spider.webmagic.Site;
import com.lingdonge.spider.webmagic.Task;
import com.lingdonge.spider.webmagic.downloader.HttpClientDownloader;
import com.lingdonge.spider.webmagic.selector.Html;
import org.junit.Test;

import java.io.UnsupportedEncodingException;


/**
 * @author code4crafer@gmail.com
 */
public class HttpClientDownloaderTest {

    public static final String PAGE_ALWAYS_NOT_EXISTS = "http://localhost:13423/404";

    @Test
    public void testDownloader() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Html html = httpClientDownloader.download("https://www.baidu.com/");
//        assertTrue(!html.getFirstSourceText().isEmpty());
    }

    //    @Test(expected = IllegalArgumentException.class)
    public void testDownloaderInIllegalUrl() throws UnsupportedEncodingException {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.download("http://www.oschina.net/>");
    }

    @Test
    public void test_download_fail() {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        Task task = Site.me().setDomain("localhost").setCycleRetryTimes(5).toTask();
        Request request = new Request(PAGE_ALWAYS_NOT_EXISTS);
        Page page = httpClientDownloader.download(request, task);
//        assertThat(page.isDownloadSuccess()).isFalse();
    }

    @Test
    public void testGetHtmlCharset() throws Exception {
//        HttpServer server = httpServer(13423);
//        server.get(by(uri("/header"))).response(header("Content-Type", "text/html; charset=gbk"));
//        server.get(by(uri("/meta4"))).response(with(text("<html>\n" +
//                "  <head>\n" +
//                "    <meta charset='gbk'/>\n" +
//                "  </head>\n" +
//                "  <body></body>\n" +
//                "</html>")),header("Content-Type","text/html; charset=gbk"));
//        server.get(by(uri("/meta5"))).response(with(text("<html>\n" +
//                "  <head>\n" +
//                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=gbk\" />\n" +
//                "  </head>\n" +
//                "  <body></body>\n" +
//                "</html>")),header("Content-Type","text/html"));
//        Runner.running(server, new Runnable() {
//            @Override
//            public void run() {
//                String charset = getCharsetByUrl("http://127.0.0.1:13423/header");
//                assertEquals(charset, "gbk");
//                charset = getCharsetByUrl("http://127.0.0.1:13423/meta4");
//                assertEquals(charset, "gbk");
//                charset = getCharsetByUrl("http://127.0.0.1:13423/meta5");
//                assertEquals(charset, "gbk");
//            }
//
//            private String getCharsetByUrl(String url) {
//                HttpClientDownloader downloader = new HttpClientDownloader();
//                Site site = Site.me();
//                CloseableHttpClient httpClient = new HttpClientGenerator().getClient(site);
//                // encoding in http header Content-Type
//                Request requestGBK = new Request(url);
//                CloseableHttpResponse httpResponse = null;
//                try {
//                    httpResponse = httpClient.execute(new HttpUriRequestConverter().convert(requestGBK, site, null).getHttpUriRequest());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                String charset = null;
//                try {
//                    byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
//                    charset = CharsetUtils.detectCharset(httpResponse.getEntity().getContentType().getValue(), contentBytes);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return charset;
//            }
//        });
    }

    @Test
    public void test_selectRequestMethod() throws Exception {
//        final int port = 13423;
//        HttpServer server = httpServer(port);
//        server.get(eq(query("q"), "spider")).response("get");
//        server.post(eq(form("q"), "spider")).response("post");
//        server.put(eq(form("q"), "spider")).response("put");
//        server.delete(eq(query("q"), "spider")).response("delete");
//        server.request(and(by(method("HEAD")),eq(query("q"), "spider"))).response(header("method","head"));
//        server.request(and(by(method("TRACE")),eq(query("q"), "spider"))).response("trace");
//        final HttpUriRequestConverter httpUriRequestConverter = new HttpUriRequestConverter();
//        final Site site = Site.me();
//        Runner.running(server, new Runnable() {
//            @Override
//            public void run() throws Exception {
//                Request request = new Request();
//                request.setUrl("http://127.0.0.1:" + port + "/search?q=spider");
//                request.setMethod(HttpConstant.Method.GET);
//                Map<String,Object> params = new HashedMap();
//                params.put("q","spider");
//                HttpUriRequest httpUriRequest = httpUriRequestConverter.convert(request,site,null).getHttpUriRequest();
////                assertThat(EntityUtils.toString(HttpClients.custom().build().execute(httpUriRequest).getEntity())).isEqualTo("get");
//                request.setMethod(HttpConstant.Method.DELETE);
//                httpUriRequest = httpUriRequestConverter.convert(request, site, null).getHttpUriRequest();
////                assertThat(EntityUtils.toString(HttpClients.custom().build().execute(httpUriRequest).getEntity())).isEqualTo("delete");
//                request.setMethod(HttpConstant.Method.HEAD);
//                httpUriRequest = httpUriRequestConverter.convert(request, site, null).getHttpUriRequest();
////                assertThat(HttpClients.custom().build().execute(httpUriRequest).getFirstHeader("method").getValue()).isEqualTo("head");
//                request.setMethod(HttpConstant.Method.TRACE);
//                httpUriRequest = httpUriRequestConverter.convert(request, site, null).getHttpUriRequest();
////                assertThat(EntityUtils.toString(HttpClients.custom().build().execute(httpUriRequest).getEntity())).isEqualTo("trace");
//                request.setUrl("http://127.0.0.1:" + port + "/search");
//                request.setMethod(HttpConstant.Method.POST);
//                request.setRequestBody(HttpRequestBody.form(params, "utf-8"));
//                httpUriRequest = httpUriRequestConverter.convert(request, site, null).getHttpUriRequest();
////                assertThat(EntityUtils.toString(HttpClients.custom().build().execute(httpUriRequest).getEntity())).isEqualTo("post");
//                request.setMethod(HttpConstant.Method.PUT);
//                httpUriRequest = httpUriRequestConverter.convert(request, site, null).getHttpUriRequest();
////                assertThat(EntityUtils.toString(HttpClients.custom().build().execute(httpUriRequest).getEntity())).isEqualTo("put");
//            }
//        });
    }

    @Test
    public void test_set_request_cookie() throws Exception {
//        HttpServer server = httpServer(13423);
//        server.get(eq(cookie("cookie"), "cookie-spider")).response("ok");
//        Runner.running(server, new Runnable() {
//            @Override
//            public void run() throws Exception {
//                HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//                Request request = new Request();
//                request.setUrl("http://127.0.0.1:13423");
//                request.addCookie("cookie","cookie-spider");
//                Page page = httpClientDownloader.download(request, Site.me().toTask());
//                assertThat(page.getRawText()).isEqualTo("ok");
//            }
//        });
    }

    @Test
    public void test_disableCookieManagement() throws Exception {
//        HttpServer server = httpServer(13423);
//        server.get(not(eq(cookie("cookie"), "cookie-spider"))).response("ok");
//        Runner.running(server, new Runnable() {
//            @Override
//            public void run() throws Exception {
//                HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//                Request request = new Request();
//                request.setUrl("http://127.0.0.1:13423");
//                request.addCookie("cookie","cookie-spider");
//                Page page = httpClientDownloader.download(request, Site.me().setDisableCookieManagement(true).toTask());
//                assertThat(page.getRawText()).isEqualTo("ok");
//            }
//        });
    }

    @Test
    public void test_set_request_header() throws Exception {
//        HttpServer server = httpServer(13423);
//        server.get(eq(header("header"), "header-spider")).response("ok");
//        Runner.running(server, new Runnable() {
//            @Override
//            public void run() throws Exception {
//                HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//                Request request = new Request();
//                request.setUrl("http://127.0.0.1:13423");
//                request.addHeader("header","header-spider");
//                Page page = httpClientDownloader.download(request, Site.me().toTask());
//                assertThat(page.getRawText()).isEqualTo("ok");
//            }
//        });
    }

    @Test
    public void test_set_site_header() throws Exception {
//        HttpServer server = httpServer(13423);
//        server.get(eq(header("header"), "header-spider")).response("ok");
//        Runner.running(server, new Runnable() {
//            @Override
//            public void run() throws Exception {
//                HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//                Request request = new Request();
//                request.setUrl("http://127.0.0.1:13423");
//                Page page = httpClientDownloader.download(request, Site.me().addHeader("header","header-spider").toTask());
//                assertThat(page.getRawText()).isEqualTo("ok");
//            }
//        });
    }

    @Test
    public void test_set_site_cookie() throws Exception {
//        HttpServer server = httpServer(13423);
//        server.get(eq(cookie("cookie"), "cookie-spider")).response("ok");
//        Runner.running(server, new Runnable() {
//            @Override
//            public void run() throws Exception {
//                HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//                Request request = new Request();
//                request.setUrl("http://127.0.0.1:13423");
//                Site site = Site.me().addCookie("cookie", "cookie-spider").setDomain("127.0.0.1");
//                Page page = httpClientDownloader.download(request, site.toTask());
//                assertThat(page.getRawText()).isEqualTo("ok");
//            }
//        });
    }

    @Test
    public void test_download_when_task_is_null() throws Exception {
//        HttpServer server = httpServer(13423);
//        server.response("foo");
//        Runner.running(server, new Runnable() {
//            @Override
//            public void run() throws Exception {
//                final HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//                Request request = new Request();
//                request.setUrl("http://127.0.0.1:13423/");
//                Page page = httpClientDownloader.download(request, Site.me().toTask());
//                assertThat(page.getRawText()).isEqualTo("foo");
//            }
//        });
    }

    @Test
    public void test_download_auth_by_SimpleProxyProvider() throws Exception {
//        HttpServer server = httpServer(13423);
//        server.get(eq(header("ModelProxy-Authorization"), "Basic dXNlcm5hbWU6cGFzc3dvcmQ=")).response("ok");
//        Runner.running(server, new Runnable() {
//            @Override
//            public void run() throws Exception {
//                HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//                httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new ModelProxy("127.0.0.1", 13423, "username", "password")));
//                Request request = new Request();
//                request.setUrl("http://www.baidu.com");
//                Page page = httpClientDownloader.download(request, Site.me().toTask());
//                assertThat(page.getRawText()).isEqualTo("ok");
//            }
//        });
    }

    @Test
    public void test_download_binary_content() throws Exception {
//        HttpServer server = httpServer(13423);
//        server.response("binary");
//        Runner.running(server, new Runnable() {
//            @Override
//            public void run() throws Exception {
//                final HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//                Request request = new Request();
//                request.setBinaryContent(true);
//                request.setUrl("http://127.0.0.1:13423/");
//                Page page = httpClientDownloader.download(request, Site.me().toTask());
//                assertThat(page.getRawText()).isNull();
//                assertThat(page.getBytes()).isEqualTo("binary".getBytes());
//            }
//        });
    }

    @Test
    public void test_download_set_charset() throws Exception {
//        HttpServer server = httpServer(13423);
//        server.response(header("Content-Type","text/html; charset=utf-8")).response("hello world!");
//        Runner.running(server, new Runnable() {
//            @Override
//            public void run() throws Exception {
//                final HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//                Request request = new Request();
//                request.setUrl("http://127.0.0.1:13423/");
//                Page page = httpClientDownloader.download(request, Site.me().toTask());
//                assertThat(page.getCharset()).isEqualTo("utf-8");
//            }
//        });
    }

    @Test
    public void test_download_set_request_charset() throws Exception {
//        HttpServer server = httpServer(13423);
//        server.response("hello world!");
//        Runner.running(server, new Runnable() {
//            @Override
//            public void run() throws Exception {
//                final HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//                Request request = new Request();
//                request.setCharset("utf-8");
//                request.setUrl("http://127.0.0.1:13423/");
//                Page page = httpClientDownloader.download(request, Site.me().setCharset("gbk").toTask());
////                assertThat(page.getCharset()).isEqualTo("utf-8");
//            }
//        });
    }

}
