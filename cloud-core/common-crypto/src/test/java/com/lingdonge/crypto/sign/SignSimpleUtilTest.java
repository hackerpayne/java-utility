package com.lingdonge.crypto.sign;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.lingdonge.core.encode.Base64Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.security.KeyPair;
import java.util.HashMap;

@Slf4j
public class SignSimpleUtilTest {

    private String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIKMPGuXOI9QcYz1i/Uu4qfg4STaJBQa7xxPkesLZpj1giwHjdLctyZZUUusQ2jKjtCIid9GP2LwRgVMcoHJhaK0jNHqxab1C4zweIKJibxB32E54ivnD0lMxGMrRulvwYy/wxjAeYj3rAlpM8jIH8KJlEIQaAjNaKZXVmmuH7FXAgMBAAECgYARG+nrO9VYyvUmwSOHjY586/b1ynUCAYmmzzLTeBoJH9vgg1jt8qk6KGiEH15yRGG1KL/Q+Dbkc5LprNww8EVIHHh2/tLhEX30HEvyTN5do6UknLYjFDX0UD4XWNAFDj9R+6LGBDljW65A20diMdId7IcAOgdykewzTKqloUcPcQJBAPeNY73VdrsKfNbVhpnJ2YJizFwZ3WGyGTjsy/YDSozCQV2aUod8hLpGTyO1RIXpgCHNEt7Zr6NQyliBa1KiLmkCQQCHALK4QNDO0puczysBSQGJbQPtHCfxMxYzTgU5KGHd46hobPrUWkpma+Gg9dgQ2Dr+kRCVFieG8cLfkUvapmm/AkEA1iJ17tXhuHWf+24E2q7h+Ylg4SJ2f3XBn54l6A58xDmctU0yqoQpg8Ah4O+B8JxE+/gapK6E4a6W+ewpW/dyUQJAHbZxn0vkqSCBT0npVScUR4LO0mjYAaYUxMXvMF1K7OMmNZWGvyYAKfqkdiClpU9x7IVQ+P8fQ/wBv+LipwTpUQJAeeWsUg9lXRAPFZlWMdqQWKA+p8IKuwxBjltqyDNSq6hJ3V3vZqv+Dbkcm6SRA0kknQA3YkpZWl2rXex+h5S1Tw==";

    private String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCCjDxrlziPUHGM9Yv1LuKn4OEk2iQUGu8cT5HrC2aY9YIsB43S3LcmWVFLrENoyo7QiInfRj9i8EYFTHKByYWitIzR6sWm9QuM8HiCiYm8Qd9hOeIr5w9JTMRjK0bpb8GMv8MYwHmI96wJaTPIyB/CiZRCEGgIzWimV1Zprh+xVwIDAQAB";

    /**
     * 需要加密的内容
     */
    private String CONTENT_ENC = "this is a test";

    @Test
    public void getSignStr() {
    }

    @Test
    public void getKeyPair() {
        KeyPair keyPair = SignSimpleUtil.getKeyPair();
        log.info("私钥为：[{}]", Base64Util.encodeToStr(keyPair.getPrivate().getEncoded()));
        log.info("公钥为：[{}]", Base64Util.encodeToStr(keyPair.getPublic().getEncoded()));
    }

    @Test
    public void encryptByPrivateKey() {

        String encrypted = SignSimpleUtil.encryptByPrivateKey(CONTENT_ENC, PRIVATE_KEY);
        log.info("私钥加密结果为：[{}]", encrypted);

        String decrpted = SignSimpleUtil.decryptByPublicKey(encrypted, PUBLIC_KEY);
        log.info("公钥解密结果为：[{}]", decrpted);
    }

    @Test
    public void encryptByPublicKey() {
        String encrypted = SignSimpleUtil.encryptByPublicKey(CONTENT_ENC, PUBLIC_KEY);
        log.info("公钥加密结果为：[{}]", encrypted);

        String decrpted = SignSimpleUtil.decryptByPrivateKey(encrypted, PRIVATE_KEY);
        log.info("私钥解密结果为：[{}]", decrpted);
    }

    @Test
    public void decryptByPrivateKey() {
    }

    @Test
    public void decryptByPublicKey() {
    }

    @Test
    public void verifySignByPublicKey() {
    }

    @Test
    public void testVerifySignByPublicKey() {
        HashMap<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("1", "11111");
        hashMap.put("2", "22222");

        String sign = SignSimpleUtil.signByPublicKey(hashMap, PUBLIC_KEY);
        log.info("公钥加签，签名结果为：[{}]", sign);

        hashMap.put("sign", sign);
//        hashMap.put("ok", "tewt");
        log.info("公钥加签还原Map结果为：[{}]", JSON.toJSONString(hashMap));

        boolean verified = SignSimpleUtil.verifySignByPrivateKey(hashMap, PRIVATE_KEY, "sign");
        log.info("私钥验签结果为：[{}]", verified ? "验签通过！" : "验签失败！！！");
    }

    @Test
    public void verifySignByPrivateKey() {
        HashMap<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("1", "11111");
        hashMap.put("2", "22222");

        String sign = SignSimpleUtil.signByPrivateKey(hashMap, PRIVATE_KEY);
        log.info("私钥加签，签名结果为：[{}]", sign);

        hashMap.put("sign", sign);
        log.info("私钥加签还原Map结果为：[{}]", JSON.toJSONString(hashMap));

        boolean verified = SignSimpleUtil.verifySignByPublicKey(hashMap, PUBLIC_KEY, "sign");
        log.info("公钥验签结果为：[{}]", verified ? "验签通过！" : "验签失败！！！");
    }

    @Test
    public void testVerifySignByPrivateKey() {
    }
}