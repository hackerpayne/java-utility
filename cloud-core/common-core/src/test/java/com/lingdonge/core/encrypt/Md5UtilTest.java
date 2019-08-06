package com.lingdonge.core.encrypt;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class Md5UtilTest {
    @Test
    public void testGetBucketId() throws Exception {

        byte[] bytes = Md5Util.getBucketId("a".getBytes(), 30);
        String newKey = new String(bytes, "utf-8");
        System.out.println("结果1：" + newKey + "===" + newKey.length());

    }

    @Test
    public void testGetMd5() throws Exception {
        String content = "a";
        String md5 = Md5Util.getMd5(content);
        String md52 = Md5Util.getMd516(content);
        String md53 = DigestUtils.md5Hex(content);
        System.out.println("结果1：" + md5 + "===" + md5.length());
        System.out.println("结果2：" + md52 + "===" + md52.length());
        System.out.println("结果2：" + md53 + "===" + md53.length());
    }

    @Test
    public void testGetMd516() throws Exception {
        String md5 = Md5Util.getMd516("北京市$朝阳区$$吉野家(双井店)$131$4823689.86,12965082.34$");
        System.out.println("结果2：" + md5);
    }

    public void testNewMd5() {


    }

}