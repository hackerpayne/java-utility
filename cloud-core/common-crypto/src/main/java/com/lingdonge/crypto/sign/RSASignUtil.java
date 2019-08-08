package com.lingdonge.crypto.sign;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用RSA做签名和验签，密钥长度默认1024
 * Java私钥为PKCS8格式，PHP使用的私钥为PKCS1格式，先将Java私钥转换为PKCS1格式，公钥不用转换，转换工具可以使用支付宝提供的签名工具
 * https://docs.open.alipay.com/291/105971/
 */
public class RSASignUtil {

    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小，单次解密最大密文长度，这里仅仅指1024bit 长度密钥
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 算法/模式/填充
     */
    public static final String CIPHER_TRANSFORMATION_RSA = "RSA/ECB/PKCS1Padding";

    /**
     * 生成公钥和私钥到Map里面
     *
     * @throws Exception
     */
    public static HashMap<String, Object> getKeys() throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        KeyPair keyPair = getKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        map.put(PUBLIC_KEY, publicKey);
        map.put(PRIVATE_KEY, privateKey);
        return map;
    }

    /**
     * 生成密钥对，公钥和私钥
     *
     * @return
     * @throws Exception
     */
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyGen.initialize(1024);//可以理解为：加密后的密文长度，实际原文要小些 越大 加密解密越慢

        // 生成密钥对
        KeyPair keyPair = keyGen.generateKeyPair();
        return keyPair;
    }

    /**
     * 得到公钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decodeBase64(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     *
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decodeBase64(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 得到密钥字符串（经过base64编码）
     *
     * @return
     */
    public static String getKeyString(Key key) {
        byte[] keyBytes = key.getEncoded();
        return Base64.encodeBase64String(keyBytes);
    }

    /**
     * 获取私钥
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 获取公钥
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.encodeBase64String(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.decodeBase64(sign));
    }


    /**
     * 使用MD5WithRSA签名
     *
     * @param content
     * @param base64PrivateKey
     * @return
     * @throws Exception
     */
    public static String getMd5Sign(String content, String base64PrivateKey) throws Exception {
        PrivateKey privateKey = getPrivateKey(base64PrivateKey);
        return getMd5Sign(content, privateKey);
    }

    /**
     * 用md5生成内容摘要，再用RSA的私钥加密，进而生成数字签名
     *
     * @param content    加密内容
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static String getMd5Sign(String content, PrivateKey privateKey) throws Exception {
        byte[] contentBytes = content.getBytes("utf-8");
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(contentBytes);
        byte[] signs = signature.sign();
        return Base64.encodeBase64String(signs);
    }

    /**
     * 使用Base64加密后的数据做验签使用
     *
     * @param content         加密内容
     * @param sign            签名
     * @param base64PublicKey base64格式的公钥
     * @return
     * @throws Exception
     */
    public static boolean verifyWhenMd5Sign(String content, String sign, String base64PublicKey) throws Exception {
        PublicKey publicKey = getPublicKey(base64PublicKey);
        return verifyWhenMd5Sign(content, sign, publicKey);
    }

    /**
     * 对用md5和RSA私钥生成的数字签名进行验证
     *
     * @param content
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verifyWhenMd5Sign(String content, String sign, PublicKey publicKey) throws Exception {
        byte[] contentBytes = content.getBytes("utf-8");
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(contentBytes);
        return signature.verify(Base64.decodeBase64(sign));
    }

    /**
     * SHA1签名
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String getSha1Sign(String content, String privateKey) throws Exception {
        PrivateKey priKey = getPrivateKey(privateKey);
        return getSha1Sign(content, priKey);
    }

    /**
     * 用sha1生成内容摘要，再用RSA的私钥加密，进而生成数字签名
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String getSha1Sign(String content, PrivateKey privateKey) throws Exception {
        byte[] contentBytes = content.getBytes("utf-8");
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey);
        signature.update(contentBytes);
        byte[] signs = signature.sign();
        return Base64.encodeBase64String(signs);
    }

    /**
     * SHA1 验签
     *
     * @param content
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verifyWhenSha1Sign(String content, String sign, String publicKey) throws Exception {
        PublicKey pubKey = getPublicKey(publicKey);
        return verifyWhenSha1Sign(content, sign, pubKey);
    }

    /**
     * 对用md5和RSA私钥生成的数字签名进行验证
     *
     * @param content
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verifyWhenSha1Sign(String content, String sign, PublicKey publicKey) throws Exception {
        byte[] contentBytes = content.getBytes("utf-8");
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(publicKey);
        signature.update(contentBytes);
        return signature.verify(Base64.decodeBase64(sign));
    }

    /**
     * 私钥解密
     *
     * @param encrypted
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String encrypted, String privateKey) throws Exception {
        return decryptByPrivateKey(encrypted, privateKey, "utf-8");
    }

    /**
     * 私钥解密
     *
     * @param encrypted  加密的数据
     * @param privateKey 解密的私钥
     * @param charset    使用的编码，默认UTF8
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String encrypted, String privateKey, String charset) throws Exception {
        byte[] encryptedData = StringUtils.isEmpty(charset) ? Base64
                .decodeBase64(encrypted.getBytes()) : Base64
                .decodeBase64(encrypted.getBytes(charset));

        byte[] decrypted = decryptByPrivateKey(encryptedData, privateKey);

        return StringUtils.isEmpty(charset) ? new String(decrypted)
                : new String(decrypted, charset);
    }

    /**
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
            throws Exception {

        PrivateKey priKey = getPrivateKey(privateKey);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_RSA);
        cipher.init(Cipher.DECRYPT_MODE, priKey);

        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥解密
     *
     * @param encrypted
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(String encrypted, String publicKey) throws Exception {
        return decryptByPublicKey(encrypted, publicKey, "utf-8");
    }

    /**
     * 公钥解密
     *
     * @param encrypted
     * @param publicKey
     * @param charset
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(String encrypted, String publicKey, String charset) throws Exception {
        byte[] encryptedData = StringUtils.isEmpty(charset) ? Base64
                .decodeBase64(encrypted.getBytes()) : Base64
                .decodeBase64(encrypted.getBytes(charset));

        byte[] decrypted = decryptByPublicKey(encryptedData, publicKey);

        return StringUtils.isEmpty(charset) ? new String(decrypted)
                : new String(decrypted, charset);
    }

    /**
     * 公钥解密
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {

        PublicKey pubKey = getPublicKey(publicKey);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_RSA);
        cipher.init(Cipher.DECRYPT_MODE, pubKey);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, String publicKey) throws Exception {
        return encryptByPublicKey(data, publicKey, "utf-8");
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @param charset
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, String publicKey, String charset) throws Exception {
        byte[] dataBytes = StringUtils.isEmpty(charset) ? data.getBytes()
                : data.getBytes(charset);

        byte[] encryptedData = Base64.encodeBase64(encryptByPublicKey(dataBytes, publicKey));

        return StringUtils.isEmpty(charset) ? new String(encryptedData)
                : new String(encryptedData, charset);
    }

    /**
     * 公钥加密
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {

        PublicKey pubKey = getPublicKey(publicKey);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 私钥加密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String encryptByPrivateKey(String data, String privateKey) throws Exception {
        return encryptByPrivateKey(data, privateKey, "utf-8");
    }

    /**
     * 私钥加密
     *
     * @param data
     * @param privateKey
     * @param charset
     * @return
     * @throws Exception
     */
    public static String encryptByPrivateKey(String data, String privateKey, String charset) throws Exception {

        byte[] dataBytes = StringUtils.isEmpty(charset) ? data.getBytes()
                : data.getBytes(charset);

        byte[] encryptedData = Base64.encodeBase64(encryptByPrivateKey(dataBytes, privateKey));

        return StringUtils.isEmpty(charset) ? new String(encryptedData)
                : new String(encryptedData, charset);

    }

    /**
     * 私钥加密
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {

        PrivateKey priKey = getPrivateKey(privateKey);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, priKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }


}
