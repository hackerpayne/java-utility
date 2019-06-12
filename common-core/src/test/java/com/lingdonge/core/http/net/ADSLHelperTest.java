package com.lingdonge.core.http.net;

import org.junit.Test;

public class ADSLHelperTest {


    @Test
    public void testConn() throws Exception {
        ADSLHelper.connAdsl("宽带", "hzhz**********", "******");
        Thread.sleep(1000);
        ADSLHelper.cutAdsl("宽带");
        Thread.sleep(1000);
        //再连，分配一个新的IP
        ADSLHelper.connAdsl("宽带", "hzhz**********", "******");
    }

}