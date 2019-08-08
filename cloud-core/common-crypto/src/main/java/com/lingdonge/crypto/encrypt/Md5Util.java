package com.lingdonge.crypto.encrypt;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * MD5密码处理类
 * Created by Kyle on 16/8/17.
 */
@Slf4j
public class Md5Util {

    private static MessageDigest digest;

    private static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    static {
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 尚未完全理解
     * http://www.cnblogs.com/colorfulkoala/p/5783556.html
     *
     * @param key
     * @param bit
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] getBucketId(byte[] key, Integer bit) throws NoSuchAlgorithmException {

        digest.update(key);
        byte[] md = digest.digest();
        byte[] r = new byte[(bit - 1) / 7 + 1];// 因为一个字节中只有7位能够表示成单字符
        int a = (int) Math.pow(2, bit % 7) - 2;
        md[r.length - 1] = (byte) (md[r.length - 1] & a);
        System.arraycopy(md, 0, r, 0, r.length);
        for (int i = 0; i < r.length; i++) {
            if (r[i] < 0) {
                r[i] &= 127;
            }
        }
        return r;
    }

    /**
     * MD5加密字符串
     *
     * @param str
     * @return
     */
    public static String getMd5(String str) {
//        byte[] btInput = str.getBytes();
//        digest.reset();
//        digest.update(btInput);
//        byte[] md = digest.digest();
//        return Hex.encodeHexString(md); // 第三方最安全

//        // 把密文转换成十六进制的字符串形式
//        int j = md.length;
//        char strChar[] = new char[j * 2];
//        int k = 0;
//        for (int i = 0; i < j; i++) {
//            byte byte0 = md[i];
//            strChar[k++] = hexDigits[byte0 >>> 4 & 0xf];
//            strChar[k++] = hexDigits[byte0 & 0xf];
//        }
//        return new String(strChar);

        return DigestUtils.md5Hex(str);
    }

    /**
     * 获取16位的MD5
     *
     * @param input
     * @return
     */
    public static String getMd516(String input) {
        return getMd5(input).substring(8, 24);
    }

    /**
     * 检查Map怎么样组合，才能生成所需要的Sign，用于破解别人的Sign签名时使用
     *
     * @param mapData
     * @param checkMd5
     * @return
     */
    public static Map<String, String> mapMd5SignCheck(Map<String, String> mapData, String checkMd5) {

        Map<String, String> mapResult = Maps.newHashMap();

        String md5;

        // 先一条一条MD5的试一下吧
        for (Map.Entry<String, String> item : mapData.entrySet()) {
            md5 = getMd5(item.getValue());
            if (md5.toUpperCase().equals(checkMd5.toUpperCase())) {
                mapResult.clear();
                mapResult.put(item.getKey(), item.getValue());
                break;
            }
        }

        // 再排个序试一下
        return mapResult;
    }


}


