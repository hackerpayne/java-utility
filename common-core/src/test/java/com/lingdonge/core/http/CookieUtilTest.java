package com.lingdonge.core.http;

import org.junit.Test;

public class CookieUtilTest {

    @Test
    public void testPringCookieStr() {

        String cookieStr = "cy=2; cye=beijing; _lxsdk_cuid=16241c7f4a5c6-04567ec097bd07-49526a-13c680-16241c7f4a63e; _lxsdk=16241c7f4a5c6-04567ec097bd07-49526a-13c680-16241c7f4a63e\n" +
                "; _hc.v=54c6d1a3-81e8-c460-3df3-3bdf6acdb8da.1521522048; s_ViewType=10; _tr.u=K8MPYZ2ibC2Ub8sP; dper\n" +
                "=6c9feed4a6fb9d1cea4aec37076f9f1304bf79e56eaf39c1fa65d2524f7e30ee; ua=%E5%B0%8F%E4%BA%94Kyle; ctu=37bb3c00ac45965a81f7a67322ec1193db0eb919d166011b57660cbefdefd6ec\n" +
                "; ll=7fd06e815b796be3df069dec7836c3df; _lxsdk_s=162ae13e26d-2d2-84f-47c%7C%7C17";

        CookieUtil.printCookieStr(cookieStr);
    }

}