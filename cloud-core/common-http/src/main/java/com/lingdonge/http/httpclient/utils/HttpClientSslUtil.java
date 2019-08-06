package com.lingdonge.http.httpclient.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * SSL相关辅助工具
 */
@Slf4j
public class HttpClientSslUtil {

    /**
     * 自动信任X509证书
     *
     * @return
     */
    public static X509TrustManager createX509TrustManager() {

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
//                return new java.security.cert.X509Certificate[]{};
                return null;
            }
        };
        return x509TrustManager;
    }

    /**
     * 绕过所有主机验证
     *
     * @return
     */
    public static HostnameVerifier createHostNameVerifier() {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        return hostnameVerifier;
    }

    /**
     * SSL证书绕过配置
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext buildSslContext() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");
        sc.init(null, new TrustManager[]{createX509TrustManager()}, null);
//        sc.init(null, new TrustManager[]{createX509TrustManager()}, new SecureRandom());
        return sc;
    }

    /**
     * 创建原生的SSLFactory
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLSocketFactory buildSslSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        return buildSslContext().getSocketFactory();
    }

    /**
     * 创建SSL认证的过滤绕过规则
     *
     * @return
     */
    public static SSLConnectionSocketFactory buildSSLConnectionSocketFactory() {
        try {
//            return new SSLConnectionSocketFactory(buildSslContext()); // 优先绕过安全证书

            // 支持TLS 1.2
            // javax.net.ssl.SSLException: Received fatal alert: protocol_version 参考：https://www.cnblogs.com/sunny08/p/8038440.html
            return new SSLConnectionSocketFactory(buildSslContext(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"}, null, createHostNameVerifier()); // 优先绕过安全证书

        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            log.error("ssl connection fail", e);
        }
        return SSLConnectionSocketFactory.getSocketFactory();
    }

}
