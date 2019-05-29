package com.lingdonge.http.webmagic.webmagic.proxy;

import com.lingdonge.http.webmagic.Site;
import com.lingdonge.http.webmagic.Task;
import com.lingdonge.http.webmagic.proxy.SimpleProxyProvider;
import com.kyle.utility.model.ModelProxy;
import org.testng.annotations.Test;

/**
 * @author code4crafter@gmail.com
 *         Date: 17/4/16
 *         Time: 上午10:29
 */
public class SimpleModelProxyProviderTest {

    public static final Task TASK = Site.me().toTask();

    @Test
    public void test_get_proxy() throws Exception {
        ModelProxy originProxy1 = new ModelProxy("127.0.0.1", 1087);
        ModelProxy originProxy2 = new ModelProxy("127.0.0.1", 1088);
        SimpleProxyProvider proxyProvider = SimpleProxyProvider.from(originProxy1, originProxy2);
        ModelProxy proxy = proxyProvider.getProxy(TASK);
//        assertThat(proxy).isEqualTo(originProxy1);
        proxy = proxyProvider.getProxy(TASK);
//        assertThat(proxy).isEqualTo(originProxy2);
        proxy = proxyProvider.getProxy(TASK);
//        assertThat(proxy).isEqualTo(originProxy1);
    }
}
