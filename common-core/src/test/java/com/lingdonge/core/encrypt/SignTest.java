package com.lingdonge.core.encrypt;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.http.HttpUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.Base64;
import org.junit.Test;

import java.util.HashMap;

@Slf4j
public class SignTest {

    private String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIGwB4UOHSRlNddKpC2tPsSt1SbWipOyGKLdeJnFPLkhYmYEiWXaHMAiMpoPIVWnEg/3BXTj3zGg5HYWG+Br7huN14X0jR5VYFUlXyWFAOm09DmRSRsNzp3ennl/TMyvLAL0EP8KA8w5t1zq+LPklElGsRY/5tto1OaFJWcnCKYRAgMBAAECgYAbCPUoWZ7dnXOx48+B7iGtoodSx1qwG2pjWUPw2lskRL9cgQmbf/KKYxalYw4R6vgq99XdXjuC/wVhUI6TJCzIpgamCl5nQjC+2dI17umHtqbIGJ4gTAQ+QieLsW1kHkekKb+XOvHKaLgMmtit4nk0/Fkjwn7W9G8x2NS7/PvTgQJBAPiw1M2aFsMJBd04nuNDwfJ2jKw6y9YXCvDJQe7xSnWmdLtCaS8SFNiAvA8yTkXr5fM78EQ9XnEv5n5EaQ2zrekCQQCFf88myoZXsNIjXc7T0TGACUw3pjiKt35sDnIyKM/YForveHvy/Bv+auIDSc/0pSzYC8z74C0egSrmMFB6GVXpAkEAt73G7MWNTbqL55/e3OECGgd29gVW9y39OlwoZ8dFEVnT40s09b8xQakTyVKMvGKHekftxC1nas9OSDp5N9NqiQJAfybl0kexv1IORFs0BPHKGO5CJvrt/cmZ4ye7QuU2WdXWzRHP1PalXyTEUmd6Z8TvOnO68OhrcROMHaVE8ZayCQJAVI5XKgl7m8Jyyhfj//I7t/5ScRopou7BmHPoCSayJbNNnGOMRr0Qymo7YP8twiiJJq92sJ0q27/6ApvLyCPabQ==";

    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBsAeFDh0kZTXXSqQtrT7ErdUm1oqTshii3XiZxTy5IWJmBIll2hzAIjKaDyFVpxIP9wV0498xoOR2Fhvga+4bjdeF9I0eVWBVJV8lhQDptPQ5kUkbDc6d3p55f0zMrywC9BD/CgPMObdc6viz5JRJRrEWP+bbaNTmhSVnJwimEQIDAQAB";

    @Test
    public void testApiSign() {
        HashMap<String, Object> hashParas = Maps.newHashMap();
        hashParas.put("mobile", "18000000000");
        hashParas.put("copId", "1");

        // 对Map进行ASCII字典排序，生成String格式，工具类由HuTool-Core提供
        String signString = MapUtil.join(MapUtil.sort(hashParas), "&", "=", false);
        log.info("预处理字符串结果为：{}", signString);

        // 使用算法进行加密，工具类由HuTool-Crypto提供
        Sign sign = SecureUtil.sign(SignAlgorithm.MD5withRSA, privateKey, null);
        String signStr = Base64.encodeBase64StringUnChunked(sign.sign(signString.getBytes()));
        log.info("MD5签名结果为：{}", signStr);

        // 将Sign签名附加到请求Map中合并提交
        hashParas.put("sign", signStr);

        // 使用Hutool-Http包，提交至接口
        String apiUrl = "http://localhost:9667/open/thirdparty/userRegister";
        String postHtml = HttpUtil.post(apiUrl, hashParas);
        log.info("POST请求结果：{}", postHtml);
    }

    /**
     * 测试加签
     */
    @Test
    public void testGenerateSign() {
        HashMap<String, Object> hashParas = Maps.newHashMap();
        hashParas.put("mobile", "18000000000");
        hashParas.put("copId", "1");
        hashParas.put("t", "12323");
        log.info("MD5签名结果为：{}", SignUtil.signMapToBase64(hashParas, privateKey));
    }

    /**
     * 测试验签
     */
    @Test
    public void testVerifySign() {

        String signResultStr = "A4EdDCFHsA/exTUTIE5ZgR4vRqr/qV+4wDbra97JgyNnpIOkLLtabGLQa/J/1BOqBuFWkYNW5FXjL8vH9SznERgAxMz2Vfats0gHV7rwV38ASc/YluCsgH1rITXV0LEtGVIQqLUWWg1KhX1m+6PuCP7kdEGCpFQbtKcmhfaxyjU=";

        HashMap<String, Object> hashParas = Maps.newHashMap();
        hashParas.put("mobile", "18000000000");
        hashParas.put("copId", "1");
        hashParas.put("sign", signResultStr);
        hashParas.put("t", "12323");

        Boolean verify = SignUtil.verifySign(hashParas, publicKey);
        log.info("验签结果：{}", verify);
    }


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
