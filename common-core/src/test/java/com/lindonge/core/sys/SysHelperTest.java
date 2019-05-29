package com.lindonge.core.sys;

import com.lindonge.core.util.Utils;
import org.testng.annotations.Test;

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