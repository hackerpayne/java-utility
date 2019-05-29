package com.lindonge.core.encrypt;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * DES加密算法
 */
public class DesEncryptUtil {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // 待加密的内容
        String content = "test";
        // DES 的加密KEy
        String desKey = "13881cf5bc03416e7e852827485e3eee";
        System.out.println("密　钥：" + desKey);
        System.out.println("加密前：" + content);
        System.out.println("============================");

        String encrypted = encryptToHexString(content, desKey);
        System.out.println("Hex加密后：" + encrypted);
        System.out.println("Hex解密后：" + decryptHex(encrypted, desKey));
    }

    /**
     * 加密返回16进制值，一般也可以使用Base64
     *
     * @param content
     * @param key
     * @return
     */
    public static String encryptToHexString(String content, String key) {
        byte[] resultHex = encrypt(content, key);
        return Hex.encodeHexString(resultHex);
    }

    /**
     * 加密
     *
     * @param content 待加密内容
     * @param key     加密的密钥
     * @return
     */
    public static byte[] encrypt(String content, String key) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            byte[] result = cipher.doFinal(content.getBytes());
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES 解密 字符串
     *
     * @param hexResult
     * @param key
     * @return
     */
    public static String decryptHex(String hexResult, String key) {

        String hexDecryResult = "";
        try {
            byte[] hexBytes = Hex.decodeHex(hexResult.toCharArray());
            hexDecryResult = decrypt(hexBytes, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return hexDecryResult;
    }

    /**
     * 解密
     *
     * @param content 待解密内容
     * @param key     解密的密钥
     * @return
     */
    public static String decrypt(byte[] content, String key) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);
            byte[] result = cipher.doFinal(content);
            return new String(result);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
