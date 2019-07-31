package com.lingdonge.core.encrypt;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import com.google.common.collect.Maps;
import org.apache.commons.net.util.Base64;
import org.junit.Test;

import java.util.HashMap;

public class SignTest {


    @Test
    public void testGetKeyPair() throws Exception {

        RSA rsa = new RSA();
        String privateKey = rsa.getPrivateKeyBase64();
        String publicKey = rsa.getPublicKeyBase64();

//        privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMJa50TT3u0ftwwUjgi9wpjxlC6XUctMvO1fO0DfUXtaUDWVMvx/ziiQIKzoiiBKciqo9e0JoLJv9T8JmnMarUXiS2P4qYGFORuJEynsgryQ42GMIWyXNHKOnj3eAMXeR2y9FFCaELY191V2WvKI/7cUfa1ub/RxiVy9mnDCki9vAgMBAAECgYBUobT3WbNHWnog/opi8A7e/sNiDm6FsqLKUp/Avf4DUu5+hruvDBA/xZMU+CW9re1M+kosr/aT/QC0NIBXAxTUCUvqeeUrt3nbwGCxmQV7tNLfrC1PJCbT0VXHqE+t0n4Ce5hmkB9Cxr4A/DY5kFW57VKG5P0JIq3vjdqaj8ruoQJBAONc5OH18GszsoNHGfnAC+xLql66XuZTyI0rwM7+VuORTNS91n67yt+NAkuBkf+t7ID8cPFKapoevQKUyk0rDUcCQQDa1bQ7SUmw0ahMZBOXxaTMk/GHrD10ZhdtIfXfWKOG52HBKilLvPN2/TpoKRvfB6y7HU2SscOdMDlTRJVgDsCZAkAZNDYHOn1BVQTH+rzNa9WmhyjGwYA9pjIIPD5uc9Fr/rJ2rui1OdoeeAI2HpHxtvUNw249wVMZ7KhlPdtDPK+HAkA8Wdd8yjdydj+t4aOtpapPaTWhLxU7trQbOtRuJinjGIjTFueQpamXVTr4Yu320k0GxYwROskx3ozQtAdmGGtxAkArR4m2OujGndIpDoRSfw/tgPiuAI5+UKvb7lM5v6Wtwe2MZ+zIGX+4v9KT1tIBSJYUM3/2wM7zGheWeraGM9w+";
//
//        publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDCWudE097tH7cMFI4IvcKY8ZQul1HLTLztXztA31F7WlA1lTL8f84okCCs6IogSnIqqPXtCaCyb/U/CZpzGq1F4ktj+KmBhTkbiRMp7IK8kONhjCFslzRyjp493gDF3kdsvRRQmhC2NfdVdlryiP+3FH2tbm/0cYlcvZpwwpIvbwIDAQAB";

        System.out.println("私钥为：");
        System.out.println(privateKey);

        System.out.println("公钥为：");
        System.out.println(publicKey);

        HashMap<String, String> hashParas = Maps.newHashMap();
        hashParas.put("mobile", "13800000000");
        hashParas.put("timestamp", "1541142830");
        hashParas.put("t", "");

        String signString = MapUtil.join(MapUtil.sort(hashParas), "&", "=", false);
        System.out.println("预处理字符串结果为：");
        System.out.println(signString);
        System.out.println("\n");

        Sign sign = SecureUtil.sign(SignAlgorithm.MD5withRSA, privateKey, publicKey);
        String signStr = Base64.encodeBase64String(sign.sign(signString.getBytes()));
        System.out.println("MD5签名结果为：");
        System.out.println(signStr);

        System.out.println("MD5验签结果为：");
        System.out.println(sign.verify(signString.getBytes(), Base64.decodeBase64(signStr)));


    }


}
