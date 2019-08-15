package com.lingdonge.crypto.encrypt;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.lingdonge.core.encode.Base64Util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES CBC加密
 */
public class AesEncryptUtil {

    // 加密用的Key 可以用26个字母和数字组成 此处使用AES-128-CBC加密模式，key需要为16位。
    private static String KEY = "!QA2Z@w1sxO*(-8L";

    private static String VECTOR = "!WFNZFU_{H%M(S|a";

    public final static String AES = "AES";


    /**
     * 加密
     *
     * @param content
     * @return
     * @throws Exception
     */
    public static String encrypt(String content) throws Exception {
        return encrypt(content, KEY, VECTOR);
    }

    /**
     * 加密
     *
     * @param content
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String key) throws Exception {
        return encrypt(content, key, VECTOR);
    }

    /**
     * 加密
     *
     * @param content
     * @param key
     * @param vector
     * @return
     * @throws Exception
     */
    public static String encrypt(String content, String key, String vector) throws Exception {
        if (key == null) {
            return null;
        }
        if (key.length() != 16) {
            return null;
        }
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(CharsetUtil.CHARSET_UTF_8), AES);
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(content.getBytes(CharsetUtil.CHARSET_UTF_8));
        return Base64.encode(encrypted);
    }

    /**
     * 解密
     *
     * @param content
     * @return
     * @throws Exception
     */
    public static String decrypt(String content) throws Exception {
        return decrypt(content, KEY, VECTOR);
    }

    /**
     * 解密
     *
     * @param content
     * @return
     * @throws Exception
     */
    public static String decrypt(String content, String key) throws Exception {
        return decrypt(content, key, VECTOR);
    }

    /**
     * 解密
     *
     * @param content
     * @param key
     * @param vector
     * @return
     * @throws Exception
     */
    public static String decrypt(String content, String key, String vector) throws Exception {
        if (key == null) {
            return null;
        }
        if (key.length() != 16) {
            return null;
        }
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), AES);
        IvParameterSpec iv = new IvParameterSpec(vector.getBytes());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] encrypted1 = Base64Util.decode(content);// 先用base64解密
        byte[] original = cipher.doFinal(encrypted1);
        return StrUtil.str(original, CharsetUtil.CHARSET_UTF_8);
    }

}
