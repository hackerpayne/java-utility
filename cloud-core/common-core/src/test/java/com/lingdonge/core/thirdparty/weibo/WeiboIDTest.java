package com.lingdonge.core.thirdparty.weibo;

import org.junit.Test;

public class WeiboIDTest {


    @Test
    public void main() {
        String aa = WeiboUtil.url2mid("zeRxPdQhO");
        System.out.println(aa);

        String bb = WeiboUtil.mid2url("4308479479485430");
        System.out.println(bb);
    }

}