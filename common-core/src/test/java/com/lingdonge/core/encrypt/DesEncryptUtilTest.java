package com.lingdonge.core.encrypt;

import org.junit.Test;

public class DesEncryptUtilTest {

    @Test
    public void test() {
        // 待加密的内容
        String content = "test";
        // DES 的加密KEy
        String desKey = "13881cf5bc03416e7e852827485e3eee";
        System.out.println("密　钥：" + desKey);
        System.out.println("加密前：" + content);
        System.out.println("============================");

        String encrypted = DesEncryptUtil.encryptToHexString(content, desKey);
        System.out.println("Hex加密后：" + encrypted);
        System.out.println("Hex解密后：" + DesEncryptUtil.decryptHex(encrypted, desKey));
    }

}