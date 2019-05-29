package com.lindonge.core.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * HtmlUnit采集处理类
 * http://htmlunit.sourceforge.net/
 * <p>
 * Created by Kyle on 16/10/11.
 */
public class HtmlUnitHelper {
    private static final Logger logger = LoggerFactory.getLogger(HtmlUnitHelper.class);
//
//    public void Test() throws IOException {
//
//        // 得到浏览器对象，直接New一个就能得到，现在就好比说你得到了一个浏览器了
//        WebClient webclient = new WebClient();
//
//        // 这里是配置一下不加载css和javaScript,配置起来很简单，是不是
//        webclient.getOptions().setCssEnabled(false);
//        webclient.getOptions().setJavaScriptEnabled(false);
//
//        // 做的第一件事，去拿到这个网页，只需要调用getPage这个方法即可
//        HtmlPage htmlpage = webclient.getPage("http://news.baidu.com/advanced_news.html");
//
//        // 根据名字得到一个表单，查看上面这个网页的源代码可以发现表单的名字叫“f”
//        final HtmlForm form = htmlpage.getFormByName("f");
//
//        // 同样道理，获取”百度一下“这个按钮
//        final HtmlSubmitInput button = form.getInputByValue("百度一下");
//
//        // 得到搜索框
//        final HtmlTextInput textField = form.getInputByName("q1");
//
//        // 最近周星驰比较火呀，我这里设置一下在搜索框内填入”周星驰“
//        textField.setValueAttribute("周星驰");
//
//        // 输入好了，我们点一下这个按钮
//        final HtmlPage nextPage = button.click();
//
//        // 我把结果转成String
//        String result = nextPage.asXml();
//
//        System.out.println(result);
//
//    }
//
//    /**
//     * 模拟登陆提交
//     *
//     * @throws IOException
//     */
//    public void SimulateSubmit() throws IOException {
//        WebClient webClient = new WebClient();
//        // 拿到这个网页
//        HtmlPage page = webClient.getPage("http://passport.tianya.cn/login.jsp");
//
//        // 填入用户名和密码
//        HtmlInput username = (HtmlInput) page.getElementById("userName");
//        username.type("ifugletest2014");
//        HtmlInput password = (HtmlInput) page.getElementById("password");
//        password.type("test123456");
//
//        // 提交
//        HtmlButton submit = (HtmlButton) page.getElementById("loginBtn");
//        HtmlPage nextPage = submit.click();
//        System.out.println(nextPage.asXml());
//    }

//    public static void testUserHttpUnit() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
//
//        /** HtmlUnit请求web页面 */
//        WebClient wc = new WebClient(BrowserVersion.CHROME);
//        wc.getOptions().setUseInsecureSSL(true);
//        wc.getOptions().setJavaScriptEnabled(true); // 启用JS解释器，默认为true
//        wc.getOptions().setCssEnabled(false); // 禁用css支持
//        wc.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
//        wc.getOptions().setTimeout(100000); // 设置连接超时时间 ，这里是10S。如果为0，则无限期等待
//        wc.getOptions().setDoNotTrackEnabled(false);
//        HtmlPage page = wc.getPage("http://www.xiu.com");
//
//        Document doc = Jsoup.parse(page.asXml());
//
//        Elements es = doc.select("script");
//        for(Element e : es){
//            if(e.toString().contains("google-analytics.com")){
//                System.out.println(e);
//            }
//        }
//	/*public static void main(String[] args) {
//		System.out.println(Test.getHtml("http://www.5118.com/seo/words/%E7%90%85%E7%90%8A%E6%A6%9C"));
//>>>>>>> .r1254
//	}*/
//    }
}
