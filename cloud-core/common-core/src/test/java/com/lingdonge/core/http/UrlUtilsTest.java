package com.lingdonge.core.http;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class UrlUtilsTest {

    @Test
    public void getUrlQueryString() {
    }

    @Test
    public void getUrlPara() {
        String url = "tcid=5&h5Token=A37770B6DD234C65A4EDB91509901405&h5Ticket=0202a6f5439c79cdb85d2f6ee6c025b4&key=lm_refuse_withdraw";
        Map<String, String> mapParams = UrlUtils.getUrlPara(url);
        System.out.println("getUrlPara结果为：");
        System.out.println(mapParams);


        url = "https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=baidu&wd=%E6%95%B0%E6%8D%AE%E5%A4%A7%E5%B1%8F%E5%8F%AF%E8%A7%86%E5%8C%96&oq=%25E5%25AE%259E%25E6%2597%25B6%25E6%2595%25B0%25E4%25BB%2593&rsv_pq=f7801f700005e15a&rsv_t=cf9ast4RGr1mGwC6qBZ497O4GKlix954N5HL%2B3xzrBbJQWtgJzRWRETK1JY&rqlang=cn&rsv_enter=1&rsv_dl=tb&inputT=6577&rsv_sug3=63&rsv_sug1=71&rsv_sug7=101&rsv_sug2=0&rsv_sug4=7588";
        mapParams = UrlUtils.getUrlPara(url);
        System.out.println("getUrlPara结果2为：");
        System.out.println(mapParams);
    }

    @Test
    public void getUrlPara1() {
    }

    @Test
    public void encodeUtf8() {
    }

    @Test
    public void encodeGbk() {
    }

    @Test
    public void encode() {
    }

    @Test
    public void decodeUrl() {
    }

    @Test
    public void decode() {
    }

    @Test
    public void urlWithForm() {
    }

    @Test
    public void urlWithForm1() {
    }

    @Test
    public void decodeParams() {
    }

    @Test
    public void getAbsUrl() {
    }

    @Test
    public void canonicalizeUrl() {
    }

    @Test
    public void isUrl() {
    }

    @Test
    public void formatUrl() {
    }

    @Test
    public void encodeIllegalCharacterInUrl() {
    }

    @Test
    public void fixIllegalCharacterInUrl() {
    }

    @Test
    public void getHostOld() {
    }

    @Test
    public void getHost() {
    }

    @Test
    public void removeProtocol() {
    }

    @Test
    public void getDomain() {
    }

    @Test
    public void removePort() {
    }
}