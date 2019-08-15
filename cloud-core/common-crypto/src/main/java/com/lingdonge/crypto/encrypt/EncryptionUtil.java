package com.lingdonge.crypto.encrypt;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * 字符串加解密算法
 */
public class EncryptionUtil {

    /**
     * 加密字符串，一个字符以上才有加密效果
     *
     * @param s 明文
     * @return 密文
     * @throws UnsupportedEncodingException
     */
    public static String encrypt(String s) throws UnsupportedEncodingException {
        // 数组拷贝
        byte[] bytes = s.getBytes(CharsetUtil.CHARSET_UTF_8);
        int length = bytes.length;

        // 第一次首尾置换并相互异或
        for (int i = 0; i < length / 2; i++) {
            byte b = bytes[length - 1 - i];
            bytes[length - 1 - i] = (byte) (bytes[i] ^ b);
            bytes[i] = b;
        }

        // 随机密钥
        byte[] key = new byte[2];
        Random random = new Random();
        random.nextBytes(key);

        // 第二次相邻置换并跟密钥异或
        for (int i = 0; i < length - 1; i = i + 2) {
            byte b = bytes[i + 1];
            bytes[i + 1] = (byte) (bytes[i] ^ key[0]);
            bytes[i] = (byte) (b ^ key[1]);
        }

        byte[] ciper = new byte[length + 2];
        System.arraycopy(bytes, 0, ciper, 1, length);

        // 密钥混合
        ciper[0] = key[0];
        ciper[ciper.length - 1] = key[1];
        return new String(ciper, "iso-8859-1");
    }

    /**
     * 解密字符串
     *
     * @param s 密文
     * @return 明文
     * @throws UnsupportedEncodingException
     */
    public static String decrypt(String s) throws UnsupportedEncodingException {
        byte[] bytes = s.getBytes("iso-8859-1");
        byte[] ciper = new byte[bytes.length - 2];
        System.arraycopy(bytes, 1, ciper, 0, ciper.length);

        int length = ciper.length;
        for (int i = 0; i < length - 1; i = i + 2) {
            byte b = ciper[i + 1];
            ciper[i + 1] = (byte) (ciper[i] ^ bytes[length + 1]);
            ciper[i] = (byte) (b ^ bytes[0]);
        }
        for (int i = 0; i < length / 2; i++) {
            byte b = ciper[i];
            ciper[i] = (byte) (ciper[length - 1 - i] ^ ciper[i]);
            ciper[length - 1 - i] = b;
        }
        return StrUtil.str(ciper, CharsetUtil.CHARSET_UTF_8);
    }

}
