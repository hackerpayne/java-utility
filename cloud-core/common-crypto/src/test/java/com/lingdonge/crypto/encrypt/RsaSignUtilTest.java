package com.lingdonge.crypto.encrypt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.lingdonge.core.http.RequestUtil;
import com.lingdonge.core.http.UrlUtils;
import com.lingdonge.crypto.sign.RsaSignUtil;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

public class RsaSignUtilTest {


    @Test
    public void testGetKeys() throws Exception {
        KeyPair hash = RsaSignUtil.getKeyPair();
        System.out.println(hash);
    }

    @Test
    public void testVerifySign() throws Exception {

        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpupi7P507Ly/kXMoIJQd84D0JaWhFMRWIS/xChY+YGTuh8c1ptxZm3dm1jVqpFUgMc2H/7IzDikLz1WTkd7kHvoyAnVdIrtmrTjh++ioUr60ukISirbma3zUmVzrpII3ScfKKRbeU0BSNjpTk5TtVyAvGwWwY8SfgQSdLc8ioNQIDAQAB";

        HashMap<String, String> hashParas = Maps.newHashMap();
        hashParas.put("mobile", "18515490065");
        hashParas.put("timestamp", "1543375150");
        String signString = RequestUtil.mapToSortedQueryString(hashParas);

        String signature = "Wf7fIph6sPhjfm63sLxwT8vNym7AkcvI0Ji61I294y%2FNqAqksmSnK7u9uHnFQvg1sZSSQABMfQRBMoC2k0tjSUX8cClf0zohrmSwDE27aLF%2FJHESR1cCtvMwf%2Bj8hrF8viUW10QrKUpHn%2FN8j41LSaKn2WJi6hTPnR%2B9wynUGeI%3D";

        String decodedSignature = UrlUtils.decode(signature, "");

        System.out.println("MD5验签结果为：");
        System.out.println(RsaSignUtil.verifyWhenMd5Sign(signString, decodedSignature, publicKey));
    }

    @Test
    public void testUrlDecode() throws UnsupportedEncodingException {
        System.out.println(UrlUtils.decodeUrl("%C3%A6%C2%B5%C2%8B%C3%A8%C2%AF%C2%95"));
        System.out.println(URLDecoder.decode("%C3%A6%C2%B5%C2%8B%C3%A8%C2%AF%C2%95", "utf-8"));

        String encoded = URLEncoder.encode("测试", "utf-8");
        System.out.println(encoded);
        System.out.println(URLDecoder.decode(encoded, "utf-8"));
    }

    @Test
    public void testJson() {
        String jsonStr = "{\"mobile\":\"13739790485\",\"timestamp\":1544695701,\"sign\":\"QCejP%2FbScSfvyaOBIOhmFDAa34n%2BzIih9HfDOITBSOY92HWB43kpx3Zp8khNUqngdcUQ%2B%2BCEL4D7sMBFzk4Zeay3H2VEu42oux3K0VRUxCp6w1%2Fha3GLf%2BfORIa78TrEnZuqhTWi687W4eMz%2FFAv6l3bQU%2Binq4x8CC0As2uyZA%3D\"}";

        JSONObject jsonObj = JSON.parseObject(jsonStr);
        Map paraMap = jsonObj;
        String data = RequestUtil.mapToSortedQueryString(paraMap);
        System.out.println("测试一：");
        System.out.println(data);

        Map<String, String> jsonObj2 = JSON.parseObject(jsonStr, new TypeReference<Map<String, String>>() {
        });
        data = RequestUtil.mapToSortedQueryString(jsonObj2);
        System.out.println("测试二：");
        System.out.println(data);
    }

    @Test
    public void testVerifySignNew() throws Exception {

        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCOj/oM+Uf8LxLg+yxrK8nQ6aPKxJE9/rNbU6XNoGY6FxbqRazaMYmYFuqQuc0Oku10H1PYKpsvdZ1XgXW81KFZa1RlEiKhGxNUenY8vLmziplfgvlHQkIkpKUx+oHvS4s0wJSmONQQRPc28M36KadzssNOq49pLz/b3/Itz0otBQIDAQAB";

        HashMap<String, String> hashParas = Maps.newHashMap();
        hashParas.put("mobile", "13940368813");
        hashParas.put("timestamp", "1544627492");
//        hashParas.put("source", UrlUtils.decode("jinliqianbao", ""));
        hashParas.put("name", "");
        hashParas.put("id_card", "");

        String signString = RequestUtil.mapToSortedQueryString(hashParas);
        System.out.println("预处理字符串(待签名)结果为：");
        System.out.println(signString);
        System.out.println("\n");

        String signature = "Y8NbURHJz%2BP0znGpqj51MWOwIBwemRFL3rdqC3D8cDG6pAwtLUi1SHKBRC6%2BPBMlgdDn3xeskQUfW8EdDESPDrx2jJTFspJsVPRonarPdMfNGS5gYtaRl4BnW5Ad4WKi6keoQQQWphPXWvbMpKC6u6kGa8pvuFdQCaJ%2BHel6sdg%3D";
        String decodedSignature = UrlUtils.decode(signature, "");
        System.out.println("MD5验签结果为：");
        System.out.println(RsaSignUtil.verifyWhenMd5Sign(signString, decodedSignature, publicKey));

    }


    @Test
    public void testGetKeyPair() throws Exception {

        HashMap<String, Object> hash = RsaSignUtil.getKeys();
        String privateKey = RsaSignUtil.getPrivateKey(hash);
        String publicKey = RsaSignUtil.getPublicKey(hash);

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

        String signString = RequestUtil.mapToSortedQueryString(hashParas);
        System.out.println("预处理字符串结果为：");
        System.out.println(signString);
        System.out.println("\n");

        String sign = RsaSignUtil.getMd5Sign(signString, privateKey);
        System.out.println("MD5签名结果为：");
        System.out.println(sign);

        System.out.println("MD5验签结果为：");
        System.out.println(RsaSignUtil.verifyWhenMd5Sign(signString, sign, publicKey));

        System.out.println("MD5验签结果2,乱数测试结果为：");
        System.out.println(RsaSignUtil.verifyWhenMd5Sign("test1231", sign, publicKey));

        String sha1Sign = RsaSignUtil.getSha1Sign(signString, privateKey);
        System.out.println("SHA1签名结果为：" + sha1Sign);

        boolean sha1Verifty = RsaSignUtil.verifyWhenSha1Sign(signString, sha1Sign, publicKey);
        System.out.println("SHA1验签结果为：" + sha1Verifty);

        System.out.println("----------私钥加密、公钥解密----------");
        String encrytped = RsaSignUtil.encryptByPrivateKey(signString, privateKey);
        System.out.println("私钥加密结果：" + encrytped);

        String decrypted = RsaSignUtil.decryptByPublicKey(encrytped, publicKey);
        System.out.println("公钥解密结果：" + decrypted);

        System.out.println("----------公钥加密、私钥解密----------");
        encrytped = RsaSignUtil.encryptByPublicKey(signString, publicKey);
        System.out.println("公钥加密结果：" + encrytped);

        decrypted = RsaSignUtil.decryptByPrivateKey(encrytped, privateKey);
        System.out.println("私钥解密结果：" + decrypted);


    }

    @Test
    public void testGetMd5Sign() throws Exception {
        System.out.println("测试生成签名");

        String privateKey = "";
        String sign = RsaSignUtil.getMd5Sign("test123", RsaSignUtil.getPrivateKey(privateKey));
        System.out.println("根据私钥生成的签名内容为：");
        System.out.println(sign);
    }

    @Test
    public void testVerifyWhenMd5Sign() {
    }

    @Test
    public void testGetSha1Sign() {
    }

    @Test
    public void testVerifyWhenSha1Sign() {
    }


}