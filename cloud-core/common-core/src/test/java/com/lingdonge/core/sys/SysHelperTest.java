package com.lingdonge.core.sys;

import com.lingdonge.core.util.Utils;
import org.junit.Test;

/**
 * Created by Kyle on 16/10/12.
 */
public class SysHelperTest {

    @Test
    public void testJvmBitVersion() throws Exception {

        String ver = Utils.JvmVersion;

        System.out.println(ver);

    }

}