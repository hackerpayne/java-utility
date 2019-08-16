package com.lingdonge.crypto.encrypt;

import org.junit.Test;

public class DesEncryptUtilTest {

    @Test
    public void test() throws Exception {
        // 待加密的内容
        String content = "d85178ffb65259f3ee58df426d2f8e26";
        // DES 的加密KEy
        String desKey = "12345678";
        System.out.println("密　钥：" + desKey);
        System.out.println("加密前：" + content);
        System.out.println("============================");

        String encrypted = DesEncryptUtil.encryptToHexString(content, desKey);
        System.out.println("Hex加密后：" + encrypted);
        System.out.println("Hex解密后：" + DesEncryptUtil.decryptHex(encrypted, desKey));
    }

}