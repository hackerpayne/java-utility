package com.lindonge.core.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * UA辅助类
 * Created by kyle on 17/2/28.
 */
public class UserAgentUtil {


    /**
     * Google爬虫的UA
     */
    public static String GoogleSpider = "";

    /**
     * 百度爬虫UA
     */
    public static String UABaiduSpider = "Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)";

    /**
     * 360爬虫
     */
    public static final String SoSpider = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0); 360Spider(compatible; HaosouSpider; http://www.haosou.com/help/help_3_2.html)";

    /**
     * 搜狗爬虫
     */
    public static final String SoGouSpider = "Sogou web spider/4.0(+http://www.sogou.com/docs/help/webmasters.htm#07)";


    public static String UAMacFirefox = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:53.0) Gecko/20100101 Firefox/53.0";

    public static String UAPc1 = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";

    public static String UAPc = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";

    public static String UAPc2 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

    public static String UAMobile = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36";

    public static String UAMobile2 = "Mozilla/5.0 (Linux; Android 8.0.0; Pixel XL Build/OPR6.170623.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.107 Mobile Safari/537.36";

    /**
     * 手机百度UA
     */
    public static String UAMobileShoujiBaidu = "Mozilla/5.0 (iPhone; CPU iPhone OS 11_2_2 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) BaiduBoxApp/10.2.2 iPhone; CPU iPhone OS 11_2_2 like Mac OS X Mobile/15C202 Safari/602.1 baiduboxapp/10.2.3.12 (Baidu; P2 11.2.2)";

    /**
     * 微信UA
     */
    public static String UAMobileWeChat = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) Mobile/14F89 MicroMessenger/6.5.10 NetType/WIFI Language/zh_CN";

    /**
     * 根据浏览器返回一个虚拟的UA
     *
     * @return
     */
    public static String getUAByBrowser() {
        String fakeUA = "";
        return fakeUA;
    }

    /**
     * 根据浏览器平台返回一个虚假的UA
     *
     * @return
     */
    public static String getUaByPlatform() {
        String fakeUA = "";
        return fakeUA;
    }


    /**
     * 从预定义的User-Agent列表中随机抽取一个返回
     *
     * @return
     */
    public static String radomUserAgent() {
        List<String> list = new ArrayList<>();
        list.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.75 Safari/537.36");
        list.add("Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.04");
        list.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
        list.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:52.0) Gecko/20100101 Firefox/52.0");
        list.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/4.0; InfoPath.2; SV1; .NET CLR 2.0.50727; WOW64)");
        list.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36");
        list.add("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
        list.add("Mozilla/4.0 (compatible; MSIE 6.0b; Windows NT 5.1)");
        list.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:24.0) Gecko/20100101 Firefox/24.0");
        list.add("Mozilla/5.0 (X11; Linux i686; rv:40.0) Gecko/20100101 Firefox/40.0");
        list.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.157 Safari/537.36");
        list.add("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)");
        list.add("Opera/9.80 (X11; Linux i686; U; ru) Presto/2.8.131 Version/11.11");
        list.add("Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25");
        list.add("Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; GTB7.4; InfoPath.2; SV1; .NET CLR 3.3.69573; WOW64; en-US)");
        list.add("Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:15.0) Gecko/20100101 Firefox/15.0.1");

        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

}
