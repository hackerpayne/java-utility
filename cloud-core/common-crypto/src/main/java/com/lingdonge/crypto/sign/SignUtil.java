package com.lingdonge.crypto.sign;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import com.lingdonge.core.collection.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.util.HashMap;

@Slf4j
public class SignUtil {

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
     *
     * @param input
     * @param privateKey
     * @return
     */
    public static byte[] encryptByPrivateKey(String input, String privateKey) {
        RSA rsa = new RSA(privateKey, null);
        return rsa.encrypt(StrUtil.bytes(input, CharsetUtil.CHARSET_UTF_8), KeyType.PrivateKey);
    }

    /**
     * 私匙加密转Base64
     *
     * @param input
     * @param privateKey
     * @return
     */
    public static String encryptByPrivateKeyToBase64(String input, String privateKey) {
        byte[] encrypted = encryptByPrivateKey(input, privateKey);
        return Base64.encodeBase64StringUnChunked(encrypted);
    }

    /**
     * 私钥加密转Hex
     *
     * @param input
     * @param privateKey
     * @return
     */
    public static String encryptByPrivateKeyToHex(String input, String privateKey) {
        byte[] encrypted = encryptByPrivateKey(input, privateKey);
        return HexUtil.encodeHexStr(encrypted);
    }

    /**
     * 公钥加密
     *
     * @param input
     * @param publicKey
     * @return
     */
    public static byte[] encryptByPublicKey(String input, String publicKey) {
        RSA rsa = new RSA(null, publicKey);
        return rsa.encrypt(StrUtil.bytes(input, CharsetUtil.CHARSET_UTF_8), KeyType.PublicKey);
    }

    /**
     * 公匙加密转Base64
     *
     * @param input
     * @param privateKey
     * @return
     */
    public static String encryptByPublicKeyToBase64(String input, String privateKey) {
        byte[] encrypted = encryptByPublicKey(input, privateKey);
        return Base64.encodeBase64StringUnChunked(encrypted);
    }

    /**
     * 公钥加密转Hex
     *
     * @param input
     * @param privateKey
     * @return
     */
    public static String encryptByPublicKeyToHex(String input, String privateKey) {
        byte[] encrypted = encryptByPublicKey(input, privateKey);
        return HexUtil.encodeHexStr(encrypted);
    }


    /**
     * Base64格式私匙解密
     *
     * @param encryptedStrBase64
     * @param privateKey
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] decryptByPrivateKeyBase64(String encryptedStrBase64, String privateKey, String charset) throws UnsupportedEncodingException {
        byte[] encryptedData = StringUtils.isEmpty(charset) ? Base64.decodeBase64(encryptedStrBase64.getBytes()) : Base64.decodeBase64(encryptedStrBase64.getBytes(charset));
        return decryptByPrivateKey(encryptedData, privateKey);
    }

    /**
     * Hex格式的公钥解密
     *
     * @param encryptedHexStr
     * @param privateKey
     * @return
     */
    public static byte[] decryptByPrivateKeyHex(String encryptedHexStr, String privateKey) {
        byte[] encryptedData = HexUtil.decodeHex(encryptedHexStr);
        return decryptByPrivateKey(encryptedData, privateKey);
    }

    /**
     * 二进制私匙解密
     *
     * @param encryptedData
     * @param privateKey
     * @return
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) {
        RSA rsa = new RSA(privateKey, null);
        return rsa.decrypt(encryptedData, KeyType.PrivateKey);
    }

    /**
     * Base64公匙解密
     *
     * @param encryptedStr
     * @param publicKey
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] decryptByPublicKeyBase64(String encryptedStr, String publicKey, String charset) throws UnsupportedEncodingException {
        byte[] encryptedData = StringUtils.isEmpty(charset) ? Base64.decodeBase64(encryptedStr.getBytes()) : Base64.decodeBase64(encryptedStr.getBytes(charset));
        return decryptByPublicKey(encryptedData, publicKey);
    }

    /**
     * Hex公匙解密
     *
     * @param encryptedHexStr
     * @param publicKey
     * @return
     */
    public static byte[] decryptByPublicKeyHex(String encryptedHexStr, String publicKey) {
        byte[] encryptedData = HexUtil.decodeHex(encryptedHexStr);
        return decryptByPublicKey(encryptedData, publicKey);
    }

    /**
     * 公匙解密
     *
     * @param encryptedData
     * @param publicKey
     * @return
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) {
        RSA rsa = new RSA(null, publicKey);
        return rsa.decrypt(encryptedData, KeyType.PublicKey);
    }

    /**
     * 生成Sign签名，Base64格式
     *
     * @param hashParas
     * @param privateKey
     * @return
     */
    public static String signMapToBase64(HashMap<String, Object> hashParas, String privateKey) {

        String signString = MapUtil.getSignStr(hashParas);
        log.debug("预处理字符串结果为：{}", signString);

        // 使用算法进行加密，工具类由HuTool-Crypto提供
        Sign sign = SecureUtil.sign(SignAlgorithm.MD5withRSA, privateKey, null);
        return Base64.encodeBase64StringUnChunked(sign.sign(signString.getBytes()));
    }

    /**
     * @param hashParas
     * @param privateKey
     * @return
     */
    public static String signMapToBase64ByPublicKey(HashMap<String, Object> hashParas, String privateKey) {
        String signString = MapUtil.getSignStr(hashParas);

        // 使用算法进行加密，工具类由HuTool-Crypto提供
        Sign sign = SecureUtil.sign(SignAlgorithm.MD5withRSA, privateKey, null);
        return Base64.encodeBase64StringUnChunked(sign.sign(signString.getBytes()));
    }

    /**
     * 使用Sign字段验签
     *
     * @param hashParas
     * @param publicKey
     * @return
     */
    public static boolean verifySign(HashMap<String, Object> hashParas, String publicKey) {
        return verifySign(hashParas, publicKey, "sign");
    }

    /**
     * 验签
     *
     * @param hashParas 参数列表
     * @param publicKey 公钥
     * @param signKey   签名 哪个Map Key里面
     * @return
     */
    public static boolean verifySign(HashMap<String, Object> hashParas, String publicKey, String signKey) {

        // 取出签名
        String signValue = hashParas.getOrDefault(signKey, "").toString();

        // 提出签名
        hashParas.remove(signKey);

        // 对Map进行ASCII字典排序，生成String格式
        String signString = MapUtil.getSignStr(hashParas);

        Sign sign = SecureUtil.sign(SignAlgorithm.MD5withRSA, null, publicKey);
        Boolean verify = sign.verify(signString.getBytes(), Base64.decodeBase64(signValue));
        return verify;
    }

}
