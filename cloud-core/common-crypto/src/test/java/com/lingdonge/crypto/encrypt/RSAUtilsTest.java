package com.lingdonge.crypto.encrypt;

import org.junit.Test;

public class RSAUtilsTest {

    @Test
    public void encryptByPublicKey() {
    }

    @Test
    public void decryptByPrivateKey() {
    }

    @Test
    public void getPublicKey() {
    }

    @Test
    public void getPrivateKey() {
    }

    @Test
    public void testEncryptByPublicKey() {
    }

    @Test
    public void testDecryptByPrivateKey() {
    }

    @Test
    public void ASCII_To_BCD() {
    }

    @Test
    public void asc_to_bcd() {
    }

    @Test
    public void bcd2Str() {
    }

    @Test
    public void splitString() {
    }

    @Test
    public void splitArray() {
    }

    @Test
    public void test() throws Exception {
        /*HashMap<String, Object> map = RSAUtils.getKeys();
        //生成公钥和私钥
        RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");

        //模
        String modulus = publicKey.getModulus().toString();
        System.err.println("modulus:" + modulus);
        //公钥指数
        String public_exponent = publicKey.getPublicExponent().toString();
        System.err.println("public_exponent:" + public_exponent);
        //私钥指数
        String private_exponent = privateKey.getPrivateExponent().toString();
        System.err.println("private_exponent:" + private_exponent);
        //明文
        String ming = "123456789";
        //使用模和指数生成公钥和私钥
        RSAPublicKey pubKey = RSAUtils.getPublicKey(modulus, public_exponent);
        RSAPrivateKey priKey = RSAUtils.getPrivateKey(modulus, private_exponent);
        //加密后的密文
        String mi = RSAUtils.encryptByPublicKey(ming, pubKey);
        System.err.println("密文："+mi);
        //解密后的明文
        ming = RSAUtils.decryptByPrivateKey(mi, priKey);
        System.err.println(ming);*/
        String jiami = RSAUtils.encryptByPublicKey("yinjihuan");
        System.out.println(jiami);
        System.out.println(RSAUtils.decryptByPrivateKey("2A531E49E6EE900173A04131F4C8E1AE25F5A8DF2E55E699A321DE6D4ACDAC6AB79B19E9EBF2A2EC505C34B3F6F96DCB242F7FDD0EEAD085A113B37AB74606E16A53CFD2374703D57EECEFC632C5C3FE9B25EA42907020B94DBEFCD83A30A07B1F869B035E6DFFDF18D965FC5B49AB074D1135F5D092EF5385DE384D7F695CE0"));

    }

}