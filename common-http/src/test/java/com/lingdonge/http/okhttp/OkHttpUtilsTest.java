package com.lingdonge.http.okhttp;

import com.kyle.utility.model.ModelProxy;
import org.jsoup.nodes.Document;
import org.testng.annotations.Test;

public class OkHttpUtilsTest {


    @Test
    public void getHtml() {
        ModelProxy proxy = new ModelProxy("192.227.198.153", 13228);
        proxy.setUsername("ninja611722");
        proxy.setPassword("QA5EJkZ06LOYH");
        Document doc = OkHttpUtils.getHtml("http://www.baidu.com", "multithreading ua", proxy);
        System.out.println(doc);
    }

    @Test
    public void getHtmlString() {
    }

    @Test
    public void getHtml1() {
    }
}
