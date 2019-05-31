package com.lingdonge.core.util;


import com.lingdonge.core.http.net.NetUtil;
import com.lingdonge.core.regex.PatternPool;
import com.lingdonge.core.regex.ReUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.InetAddress;

/**
 * NetUtil单元测试
 *
 * @author Looly
 */
public class NetUtilTest {

    @Test
    public void getLocalhostTest() {
        InetAddress localhost = NetUtil.getLocalhost();
        Assert.assertNotNull(localhost);
    }

    @Test
    public void getLocalMacAddressTest() {
        String macAddress = NetUtil.getLocalMacAddress();
        Assert.assertNotNull(macAddress);

        //验证MAC地址正确
        boolean match = ReUtil.isMatch(PatternPool.MAC_ADDRESS, macAddress);
        Assert.assertTrue(match);
    }
}
