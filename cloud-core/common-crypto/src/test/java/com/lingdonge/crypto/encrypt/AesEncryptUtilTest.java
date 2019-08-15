package com.lingdonge.crypto.encrypt;

import org.junit.Test;

public class AesEncryptUtilTest {

    @Test
    public void encrypt() throws Exception {
        // 需要加密的字串
        String cSrc = "我爱你";

        // 加密
        long lStart = System.currentTimeMillis();
        String enString = AesEncryptUtil.encrypt(cSrc, "!QA2Z@w1sxO*(-8L");
        System.out.println("加密后的字串是：" + enString);

        long lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("加密耗时：" + lUseTime + "毫秒");
        // 解密
        lStart = System.currentTimeMillis();
        String DeString = AesEncryptUtil.decrypt(enString);
        System.out.println("解密后的字串是：" + DeString);
        lUseTime = System.currentTimeMillis() - lStart;
        System.out.println("解密耗时：" + lUseTime + "毫秒");

    }

    @Test
    public void testEncrypt() {
    }

    @Test
    public void testEncrypt1() {
    }

    @Test
    public void decrypt() {
    }

    @Test
    public void testDecrypt() {
    }

    @Test
    public void testDecrypt1() {
    }
}