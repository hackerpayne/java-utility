package com.lindonge.core.util;


import com.lindonge.core.http.net.NetUtil;
import com.lindonge.core.regex.PatternPool;
import com.lindonge.core.regex.ReUtil;
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
