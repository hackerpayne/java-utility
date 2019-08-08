package com.lingdonge.crypto.sign;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.Base64;

import java.util.HashMap;

@Slf4j
public class SignUtil {

    /**
     * 生成Sign签名，Base64格式
     *
     * @param hashParas
     * @param privateKey
     * @return
     */
    public static String signMapToBase64(HashMap<String, Object> hashParas, String privateKey) {
        // 对Map进行ASCII字典排序，生成String格式，工具类由HuTool-Core提供
        String signString = MapUtil.join(MapUtil.sort(hashParas), "&", "=", false);
        log.debug("预处理字符串结果为：{}", signString);

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
        String signString = MapUtil.join(MapUtil.sort(hashParas), "&", "=", false);

        Sign sign = SecureUtil.sign(SignAlgorithm.MD5withRSA, null, publicKey);
        Boolean verify = sign.verify(signString.getBytes(), Base64.decodeBase64(signValue));
        return verify;
    }

}
