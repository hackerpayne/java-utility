package com.lingdonge.core.http;

import java.net.InetAddress;
import java.net.Socket;

/**
 **/
public class NetUtils {

    /**
     * 检查本地端口是否被占用
     *
     * @param port
     * @return
     */
    public static boolean isLoclePortUsing(int port) {
        boolean flag = true;
        try {
            flag = isPortUsing("127.0.0.1", port);
        } catch (Exception e) {
        }
        return flag;
    }

    /**
     * 检查指定主机的端口是否被占用
     *
     * @param host
     * @param port
     * @return
     */
    public static boolean isPortUsing(String host, int port) {
        boolean flag = false;
        try {
            InetAddress theAddress = InetAddress.getByName(host);
            Socket socket = new Socket(theAddress, port);
            flag = true;
        } catch (Exception e) {

        }
        return flag;
    }
}
