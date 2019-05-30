package com.lindonge.core.http;

import com.lindonge.core.bean.common.ModelProxy;
import com.lindonge.core.regex.PatternPool;
import com.lindonge.core.regex.ReUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

/**
 * Created by kyle on 2017/7/12.
 */
public class ProxyUtil {
    private static final Logger logger = LoggerFactory.getLogger(ProxyUtil.class);

    /**
     * 判断是否为有效的代理IP
     *
     * @param input
     * @return
     */
    public static boolean isValidProxy(String input) {
        return ReUtil.isMatch(PatternPool.PLAIN_PROXY, input);
    }

    /**
     * 从HTML中解析出带端口的IP代理格式
     * 处理方法是把所有HTML标签替换为：号，然后正则去除多个：号，留下一个，然后进行匹配。
     *
     * @param html
     * @return
     */
    public static String parseProxy(String html) {

        String text = ReUtil.replaceAll(html, PatternPool.HTML_TAG, ":");
        text = text.replace("\r\n", ":").replace("\r", ":").replace("\n", ":");
        text = text.replace(" ", "");
        text = ReUtil.replaceAll(text, "(:{1,})", ":");

        text = ReUtil.get(PatternPool.PLAIN_PROXY, text, 0);
        return text;
    }

    /**
     * 解析URL到Model里面
     *
     * @param proxy
     * @return
     */
    public static ModelProxy parseModelProxy(String proxy) {
        String[] proxyArray = proxy.split("@");
        String[] userArray = proxyArray[0].split(":");
        String[] HostArray = proxyArray[1].split(":");

        ModelProxy modelProxy = new ModelProxy();
        modelProxy.setHost(HostArray[0]);
        modelProxy.setPort(Integer.parseInt(HostArray[1]));
        modelProxy.setUsername(userArray[0]);
        modelProxy.setPassword(userArray[1]);

        return modelProxy;
    }


    /**
     * 校验代理IP的有效性，测试地址为：http://www.ip138.com
     *
     * @param ip   代理IP地址
     * @param port 代理IP端口
     * @return 此代理IP是否有效
     */
    public static boolean checkValidIP(String ip, Integer port) {
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL("http://www.ip138.com");
            //代理服务器
            InetSocketAddress proxyAddr = new InetSocketAddress(ip, port);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddr);
            connection = (HttpURLConnection) url.openConnection(proxy);
            connection.setReadTimeout(4000);
            connection.setConnectTimeout(4000);
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == 200) {
                connection.disconnect();
                return true;
            }

        } catch (Exception e) {
            connection.disconnect();
            return false;
        }
        return false;
    }

    /**
     * 检查代理是否有效
     *
     * @param p
     * @return
     */
    public static boolean validateProxy(ModelProxy p) {
        Socket socket = null;
        try {
            socket = new Socket();
            InetSocketAddress endpointSocketAddr = new InetSocketAddress(p.getHost(), p.getPort());
            socket.connect(endpointSocketAddr, 3000);
            return true;
        } catch (IOException e) {
            logger.warn("FAILRE - CAN not connect!  remote: " + p);
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.warn("Error occurred while closing socket of validating proxy", e);
                }
            }
        }

    }

    public static void main(String[] args) {

        String html = "<tr>\n" +
                "                    <td>122.243.13.223</td>\n" +
                "                    <td>9000</td>\n" +
                "                    <td>普匿</td>\n" +
                "\t\t    <td>HTTP</td>\n" +
                "\t\t    <td>0.31 秒</td>\n" +
                "\t\t    <td>浙江省金华市 电信</td>\n" +
                "\t\t    <td>6分钟8秒前</td>\n" +
                "                </tr>";

        System.out.println("代理结果为：" + parseProxy(html));
    }

}
