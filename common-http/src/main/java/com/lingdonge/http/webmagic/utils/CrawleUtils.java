package com.lingdonge.http.webmagic.utils;

import com.alibaba.fastjson.JSON;
import com.lingdonge.core.encrypt.Md5Util;
import com.lingdonge.core.http.HtmlUtil;
import com.lingdonge.core.http.UserAgentUtil;
import com.lingdonge.core.bean.common.ModelProxy;
import com.lingdonge.core.util.StringUtils;
import com.lingdonge.http.jsoupxpath.exception.XpathSyntaxErrorException;
import com.lingdonge.http.jsoupxpath.model.JXDocument;
import com.lingdonge.http.jsoupxpath.model.JXNode;
import com.lingdonge.http.webmagic.Request;
import com.lingdonge.http.webmagic.Site;
import com.lingdonge.http.webmagic.Spider;
import com.lingdonge.http.webmagic.config.CloudProxyConfig;
import com.lingdonge.http.webmagic.config.SpiderConfig;
import com.lingdonge.http.webmagic.downloader.HttpClientDownloaderNew;
import com.lingdonge.http.webmagic.proxy.SimpleProxyProvider;
import com.lingdonge.http.webmagic.selector.Html;
import com.lingdonge.http.webmagic.selector.Selectable;
import com.lingdonge.redis.service.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 爬虫常用辅助类
 */
@Slf4j
public class CrawleUtils {

    public static void main(String[] args) throws Exception {

        log.info("Done 处理完成");
    }

    /**
     * 打印所有列表的信息
     *
     * @param links
     */
    public static void printLinks(List<String> links) {
        log.info("开始打印列表：共：" + links.size() + "条");
        for (String item : links) {
            log.info(item);
        }

        log.info("列表打印完毕，共：" + links.size() + "条");
    }

    /**
     * 从节点里面解析文本出来
     *
     * @param item
     * @return
     */
    public static String getText(Selectable item) {
        return item != null ? Jsoup.parse(item.get()).text() : "";//系统自带的解析不了，使用Jsoup进行解析
    }

    /**
     * 检查Request是否有额外的信息
     *
     * @param request
     * @return
     */
    public static boolean checkForAdditionalInfo(Request request) {
        if (request == null) {
            return false;
        }

        if (!request.getHeaders().isEmpty() || !request.getCookies().isEmpty()) {
            return true;
        }

        if (StringUtils.isNotBlank(request.getCharset()) || StringUtils.isNotBlank(request.getMethod())) {
            return true;
        }

        if (request.isBinaryContent() || request.getRequestBody() != null) {
            return true;
        }

        if (request.getExtras() != null && !request.getExtras().isEmpty()) {
            return true;
        }
        if (request.getPriority() != 0L) {
            return true;
        }

        return false;
    }

    private static RedisPoolUtil redisPoolUtil;

    /**
     * 清理Redis里面的记录，防止海量采集的时候Redis的内存超标
     * 注意：此处的uuid必须是PageProcessor设置domain之后取出来的，否则可能会报错。因为默认的是使用随机数
     *
     * @param uuid
     * @param request
     * @throws Exception
     */
    public synchronized static void clearItem(String uuid, Request request) {

        try {
            log.info(StringUtils.format("开始清理Redis数据Key:[{}],Field:[{}]", "item_" + uuid, request.getUrl()));
            redisPoolUtil = RedisPoolUtil.getInstance();
            redisPoolUtil.hdel("item_" + uuid, Md5Util.getMd516(request.getUrl()));
        } catch (Exception e) {
            log.error("clearItem发生异常：", e);
        }
    }

    /**
     * 设置下载的代理配置
     *
     * @param spider       爬虫
     * @param abuyunConfig 云代理配置
     * @return
     */
    public static Spider setDownloaderProxyBy(Spider spider, CloudProxyConfig abuyunConfig) {

        // 配置代理IP池
        HttpClientDownloaderNew httpClientDownloader = new HttpClientDownloaderNew();
//                httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new ModelProxy("http-dyn.abuyun.com", 9020, "HK05YRH0E4J7JV5D", "E7F530271C159D30")));
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new ModelProxy(abuyunConfig.getHost(), abuyunConfig.getPort(), abuyunConfig.getKey(), abuyunConfig.getSecret())));

        //设置 下载器
        spider.setDownloader(httpClientDownloader);


        return spider;
    }

    /**
     * 设置默认站点配置
     *
     * @return
     */
    public static Site getDefaultSite() {
        return Site.me()
//                .addHeader("Accept-Encoding", "/") //解决一些网站的编码出错问题
                .setRetryTimes(5)
                .setCycleRetryTimes(5)
                .setUserAgent(UserAgentUtil.UABaiduSpider)
                .setSleepTime(500) // 设置 每个请求之间的间隔时间，设定为0之后特别容易http 429异常
                .setRetrySleepTime(30000) // 设置重试间隔时间
                .setTimeOut(30000) //设置HTTP请求超时时间，使用代理的话，最好30秒
                .setCharset("utf-8");// 设置 HTML编码方式

    }

    /**
     * 字符串解析为Webmagic能处理的Html格式
     *
     * @param input
     * @return
     */
    public static Html getHtml(String input) {
//        return new Html(input);
        return Html.create(input);
    }

    /**
     * 使用JsoupXpath解析
     *
     * @param input
     * @param xpath
     * @return
     */
    public static List<JXNode> getXpathList(String input, String xpath) {
        JXDocument document = new JXDocument(input);
        try {
            return document.selN(xpath);
        } catch (XpathSyntaxErrorException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 使用Xpath解析一个元素
     *
     * @param input
     * @param xpath
     * @return
     */
    public static JXNode getXpath(String input, String xpath) {
        JXDocument document = new JXDocument(input);
        try {
            return document.selNOne(xpath);
        } catch (XpathSyntaxErrorException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 清理A标签
     *
     * @param input
     * @return
     */
    public static String clearATag(String input) {
        return HtmlUtil.clearATag(Jsoup.clean(input, Whitelist.basicWithImages()));
    }

    /**
     * 加载配置文件
     *
     * @param input
     * @return
     */
    public static SpiderConfig loadSpiderConfig(String input) {
        return JSON.parseObject(input, SpiderConfig.class);
    }

    /**
     * 获取配置文件
     *
     * @param spiderConfig
     * @return
     */
    public static String getSpiderConfigStr(SpiderConfig spiderConfig) {
        return JSON.toJSONString(spiderConfig);
    }


    public static List<Request> convertToRequests(Collection<String> urls) {
        List<Request> requestList = new ArrayList<Request>(urls.size());
        for (String url : urls) {
            requestList.add(new Request(url));
        }
        return requestList;
    }

    public static List<String> convertToUrls(Collection<Request> requests) {
        List<String> urlList = new ArrayList<String>(requests.size());
        for (Request request : requests) {
            urlList.add(request.getUrl());
        }
        return urlList;
    }
}
