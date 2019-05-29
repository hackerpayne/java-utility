package com.lindonge.core.encrypt;

import com.lindonge.core.http.UrlUtils;
import org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class RsaSimpleSignTest {


    public static final String KEY_ALGORITHM = "RSA";
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

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


    public static void main(String[] args) throws Exception {
        String sign = "QCejP%2FbScSfvyaOBIOhmFDAa34n%2BzIih9HfDOITBSOY92HWB43kpx3Zp8khNUqngdcUQ%2B%2BCEL4D7sMBFzk4Zeay3H2VEu42oux3K0VRUxCp6w1%2Fha3GLf%2BfORIa78TrEnZuqhTWi687W4eMz%2FFAv6l3bQU%2Binq4x8CC0As2uyZA%3D";

        // 17604135596&1544604184&&&LhdN27Be9Df93lWCHLj1QKm4qR94KqtevXDjLblsa61rc8h8a0IAfqF9CCF%2FL1xI540r1GpNL0klYwnYmHQmVrHV%2Bn4GPLz2JHCqv7wyo2m3E4fEwUJhQRZFxWsjQfwTo3YEay6L%2FwnxEzuYYBMtuctbX9Dm%2BBHsBfUAG4myEUw%3D
        String content = "mobile=13739790485&timestamp=1544695701";

        //测试公钥
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCOj/oM+Uf8LxLg+yxrK8nQ6aPKxJE9/rNbU6XNoGY6FxbqRazaMYmYFuqQuc0Oku10H1PYKpsvdZ1XgXW81KFZa1RlEiKhGxNUenY8vLmziplfgvlHQkIkpKUx+oHvS4s0wJSmONQQRPc28M36KadzssNOq49pLz/b3/Itz0otBQIDAQAB";

        boolean flag = verifyWhenMd5Sign(content, UrlUtils.decode(sign,"utf-8"), publicKey);
        System.out.println(flag?"验签成功！":"验签失败！！！");
    }
}
