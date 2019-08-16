package com.lingdonge.crypto.sign;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.lingdonge.core.collection.MapUtil;
import com.lingdonge.core.encode.EncodeUtil;
import com.lingdonge.core.enums.EncodingTypeEnum;
import lombok.extern.slf4j.Slf4j;

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
     * @param encodingTypeEnum 转成目标的格式
     * @return
     */
    public static String encryptByPrivateKey(String input, String privateKey, EncodingTypeEnum encodingTypeEnum) {
        byte[] encrypted = encryptByPrivateKey(input, privateKey);
        return EncodeUtil.encodeTo(encrypted, encodingTypeEnum);
    }

    /**
     * 生成Sign签名，Base64格式
     *
     * @param hashParas
     * @param privateKey
     * @return
     */
    public static String encryptByPrivateKey(HashMap<String, Object> hashParas, String privateKey, EncodingTypeEnum encodingTypeEnum) {

        String signString = MapUtil.getSignStr(hashParas);

        // 使用算法进行加密，工具类由HuTool-Crypto提供
//        Sign sign = SecureUtil.sign(SignAlgorithm.MD5withRSA, privateKey, null);
//        byte[] signByte = sign.sign(signString.getBytes());

        return EncodeUtil.encodeTo(encryptByPrivateKey(signString, privateKey), encodingTypeEnum);
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
     * @param publicKey
     * @return
     */
    public static String encryptByPublicKey(String input, String publicKey, EncodingTypeEnum encodingTypeEnum) {
        byte[] encrypted = encryptByPublicKey(input, publicKey);
        return EncodeUtil.encodeTo(encrypted, encodingTypeEnum);
    }

    /**
     * @param hashParas
     * @param publicKey
     * @param encodingTypeEnum
     * @return
     */
    public static String encryptByPublicKey(HashMap<String, Object> hashParas, String publicKey, EncodingTypeEnum encodingTypeEnum) {
        String signString = MapUtil.getSignStr(hashParas);

        // 使用算法进行加密，工具类由HuTool-Crypto提供
//        Sign sign = SecureUtil.sign(SignAlgorithm.MD5withRSA, publicKey, null);
//        return Base64.encodeBase64StringUnChunked(sign.sign(signString.getBytes()));

        return EncodeUtil.encodeTo(encryptByPublicKey(signString, publicKey), encodingTypeEnum);
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
     * Base64格式私匙解密
     *
     * @param encryptedStr
     * @param privateKey
     * @return
     */
    public static byte[] decryptByPrivateKey(String encryptedStr, String privateKey) {
        byte[] encryptedData = SecureUtil.decode(encryptedStr);
        return decryptByPrivateKey(encryptedData, privateKey);
    }

    /**
     * @param encryptedStr
     * @param privateKey
     * @param encodingTypeEnum
     * @return
     */
    public static String decryptToStrByPrivateKey(String encryptedStr, String privateKey, EncodingTypeEnum encodingTypeEnum) {
        return EncodeUtil.encodeTo(decryptByPrivateKey(encryptedStr, privateKey), encodingTypeEnum);
    }

    /**
     * 公匙解密: byte[] - byte[]
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
     * 公匙解密: str - byte[]
     *
     * @param encryptedStr
     * @param publicKey
     * @return
     */
    public static byte[] decryptByPublicKey(String encryptedStr, String publicKey) {
        byte[] encryptedData = SecureUtil.decode(encryptedStr);
        return decryptByPublicKey(encryptedData, publicKey);
    }

    /**
     * 公匙解密：str - str
     *
     * @param encryptedStr
     * @param publicKey
     * @param encodingTypeEnum
     * @return
     */
    public static String decryptToStrByPublicKey(String encryptedStr, String publicKey, EncodingTypeEnum encodingTypeEnum) {
        return EncodeUtil.encodeTo(decryptByPublicKey(encryptedStr, publicKey), encodingTypeEnum);
    }

    /**
     * 使用Sign字段验签
     *
     * @param hashParas
     * @param publicKey
     * @return
     */
    public static boolean verifySignByPublicKey(HashMap<String, Object> hashParas, String publicKey) {
        return verifySignByPublicKey(hashParas, publicKey, "sign", EncodingTypeEnum.BASE64);
    }

    /**
     * 验签
     *
     * @param hashParas 参数列表
     * @param publicKey 公钥
     * @param signKey   签名 哪个Map Key里面
     * @return
     */
    public static boolean verifySignByPublicKey(HashMap<String, Object> hashParas, String publicKey, String signKey, EncodingTypeEnum encodingTypeEnum) {

        String signValue = MapUtil.getStr(hashParas, "sign"); // 取出签名

        String signString = MapUtil.getSignStr(hashParas, signKey); // 除出签名之后的字符串列表
        String signBase64 = encryptByPublicKey(signString, publicKey, encodingTypeEnum); // 生成签名

//        Sign sign = SecureUtil.sign(SignAlgorithm.MD5withRSA, null, publicKey);
//        Boolean verify = sign.verify(signString.getBytes(), Base64.decodeBase64(signValue));

        return signBase64.equalsIgnoreCase(signValue); // 生成签名与取出的签名对比
    }

    /**
     * 使用Sign字段验签
     *
     * @param hashParas
     * @param privateKey
     * @return
     */
    public static boolean verifySignByPrivateKey(HashMap<String, Object> hashParas, String privateKey) {
        return verifySignByPrivateKey(hashParas, privateKey, "sign", EncodingTypeEnum.BASE64);
    }

    /**
     * 验签
     *
     * @param hashParas  参数列表
     * @param privateKey 公钥
     * @param signKey    签名 哪个Map Key里面
     * @return
     */
    public static boolean verifySignByPrivateKey(HashMap<String, Object> hashParas, String privateKey, String signKey, EncodingTypeEnum encodingTypeEnum) {


        String signValue = MapUtil.getSignStr(hashParas, signKey); // 取出签名

        String signString = MapUtil.getSignStr(hashParas, signKey); // 除出签名之后的字符串列表
        String signBase64 = encryptByPrivateKey(signString, privateKey, encodingTypeEnum); // 生成签名

//        Sign sign = SecureUtil.sign(SignAlgorithm.MD5withRSA, null, publicKey);
//        Boolean verify = sign.verify(signString.getBytes(), Base64.decodeBase64(signValue));

        return signBase64.equalsIgnoreCase(signValue); // 生成签名与取出的签名对比
    }
}
