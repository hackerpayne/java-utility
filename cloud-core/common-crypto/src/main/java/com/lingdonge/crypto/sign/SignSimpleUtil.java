package com.lingdonge.crypto.sign;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 简化版的加签验签方案
 */
public class SignSimpleUtil {

    /**
     * Map排序生成签名列表字符 Popup弹出指定的数据，Map排序
     *
     * @param hashParameters
     * @param popKey
     * @return
     */
    public static String getSignStr(Map<String, Object> hashParameters, String popKey) {

        if (MapUtil.isEmpty(hashParameters)) {
            return null;
        }

        Map<String, Object> newHash = hashParameters.entrySet().stream()
                .filter(item -> !item.getKey().equalsIgnoreCase(popKey))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (s, b) -> s));

        // 对Map进行ASCII字典排序，生成String格式，工具类由HuTool-Core提供
        return MapUtil.join(MapUtil.sort(newHash), "&", "=", false);
    }

    /**
     * 生成公私匙对
     *
     * @return
     */
    public static KeyPair getKeyPair() {
        return SecureUtil.generateKeyPair("RSA");
    }

    /**
     * 私匙加密
     * input->bytes->enc->base64
     *
     * @param input
     * @param privateKey
     * @return
     */
    public static String encryptByPrivateKey(String input, String privateKey) {
        RSA rsa = new RSA(privateKey, null);
        byte[] encrypted = rsa.encrypt(StrUtil.bytes(input), KeyType.PrivateKey);
        return Base64.encode(encrypted);
    }

    /**
     * 公钥加密
     *
     * @param input
     * @param publicKey
     * @return
     */
    public static String encryptByPublicKey(String input, String publicKey) {
        RSA rsa = new RSA(null, publicKey);
        byte[] encrypted = rsa.encrypt(StrUtil.bytes(input, CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
        return Base64.encode(encrypted);
    }

    /**
     * 二进制私匙解密
     *
     * @param encryptedData
     * @param privateKey
     * @return
     */
    public static String decryptByPrivateKey(String encryptedData, String privateKey) {
        RSA rsa = new RSA(privateKey, null);
        byte[] decrypted = rsa.decrypt(SecureUtil.decode(encryptedData), KeyType.PrivateKey);
        return StrUtil.str(decrypted, CharsetUtil.UTF_8);
    }

    /**
     * 公匙解密: byte[] - byte[]
     *
     * @param encryptedData
     * @param publicKey
     * @return
     */
    public static String decryptByPublicKey(String encryptedData, String publicKey) {
        RSA rsa = new RSA(null, publicKey);
        byte[] decrypted = rsa.decrypt(SecureUtil.decode(encryptedData), KeyType.PublicKey);
        return StrUtil.str(decrypted, CharsetUtil.UTF_8);
    }

    /**
     * 私钥生成签名
     *
     * @param hashMap
     * @param privateKey 私钥
     * @return
     */
    public static String signByPrivateKey(HashMap<String, Object> hashMap, String privateKey) {
        String signStr = getSignStr(hashMap, "");
        return encryptByPrivateKey(signStr, privateKey);
    }

    /**
     * 公钥生成签名
     *
     * @param hashMap
     * @param publicKey 公钥
     * @return
     */
    public static String signByPublicKey(HashMap<String, Object> hashMap, String publicKey) {
        String signStr = getSignStr(hashMap, "");
        return encryptByPublicKey(signStr, publicKey);
    }

    /**
     * 使用Sign字段验签
     *
     * @param hashParas
     * @param publicKey
     * @return
     */
    public static boolean verifySignByPublicKey(HashMap<String, Object> hashParas, String publicKey) {
        return verifySignByPublicKey(hashParas, publicKey, "sign");
    }

    /**
     * 验签
     *
     * @param hashParas 参数列表
     * @param publicKey 公钥
     * @param signKey   签名 哪个Map Key里面
     * @return
     */
    public static boolean verifySignByPublicKey(HashMap<String, Object> hashParas, String publicKey, String signKey) {
        String signValue = MapUtil.getStr(hashParas, "sign"); // 取出签名
        String signString = getSignStr(hashParas, signKey); // 除出签名之后的字符串列表
        String signStringVerify = decryptByPublicKey(signValue, publicKey); // 使用公钥解开数据
        return signStringVerify.equalsIgnoreCase(signString); // 解开的数据与要生成的数据一致，则代表验签成功
    }

    /**
     * 使用Sign字段验签
     *
     * @param hashParas
     * @param privateKey
     * @return
     */
    public static boolean verifySignByPrivateKey(HashMap<String, Object> hashParas, String privateKey) {
        return verifySignByPrivateKey(hashParas, privateKey, "sign");
    }

    /**
     * 验签
     *
     * @param hashParas  参数列表
     * @param privateKey 公钥
     * @param signKey    签名 哪个Map Key里面
     * @return
     */
    public static boolean verifySignByPrivateKey(HashMap<String, Object> hashParas, String privateKey, String signKey) {

        String signValue = MapUtil.getStr(hashParas, signKey); // 取出签名
        String signString = getSignStr(hashParas, signKey); // 除出签名之后的字符串列表
        // 之前的方法
        // String signBase64 = encryptByPrivateKey(signString, privateKey); // 生成签名
        // return signBase64.equalsIgnoreCase(signValue); // 生成签名与取出的签名对比
        // 修改后的方法可以验签成功(待确认)
        String signStringVerify = decryptByPrivateKey(signValue, privateKey); // 私钥解密
        return signString.equalsIgnoreCase(signStringVerify); // 生成签名与取出的签名对比
    }

}
