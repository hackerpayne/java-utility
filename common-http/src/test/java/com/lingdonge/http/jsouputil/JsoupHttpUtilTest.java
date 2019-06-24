package com.lingdonge.http.jsouputil;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;

@Slf4j
public class JsoupHttpUtilTest {

    @Test
    public void testGetHtml() {
        String html = JsoupHttpUtil.getHtmlStr("http://land.rongxiaoyu.com");
        log.info("HTML结果为：" + html);
    }

    /**
     * 获取HTML结果
     *
     * @return
     * @throws IOException
     */
    @Test
    public void testPostHtmlStr() throws IOException {

        String url = "http://www.baidu.com";

        //获取 Cookie
        Connection.Response res = Jsoup.connect(url)
                .method(Connection.Method.GET)
                .execute();
//        String sessionId = res.cookie(sessionName);
//        System.err.println(sessionId);

        //登录请求提交
        Connection.Response login = Jsoup.connect("loginAction.do")
//                .header("Cookie", sessionName + "=" + sessionId)  //携带刚才的 Cookie 信息
                .data("zjh", "账号", "mm", "密码")
                //这里的 zjh 和 mm 就是登录页面 form 表单的 name
                .method(Connection.Method.POST)
                .execute();

        //此时 sessionId 为可用状态
        Document scoreDoc = Jsoup.connect(url)
//                .cookie(sessionName, sessionId)
                .get();
        System.err.println(scoreDoc);

        String html = scoreDoc.body().html();

        log.info(html);
    }

}