package com.lingdonge.core.encode;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * Base64 一般有3种实现：
 * sun.misc套件下的BASE64Encoder和BASE64Decoder
 * Apache Commons Codec有提供Base64的编码与解码功能，会使用到org.apache.commons.codec.binary套件下的Base64类别
 * Java 8的java.util套件中，新增了Base64的类别，可以用来处理Base64的编码与解码
 * <p>
 * Java8自带的性能最好。最好直接使用
 * Java 8提供的Base64，要比sun.mis c套件提供的还要快至少11倍，比Apache Commons Codec提供的还要快至少3倍。因此在Java上若要使用Base64，这个Java 8底下的java .util套件所提供的Base64类别绝对是首选！
 */
public class Base64Util {

    private static final Encoder ENCODER = Base64.getEncoder();

    private static final Encoder URL_ENCODER = Base64.getUrlEncoder();

    private static final Decoder DECODER = Base64.getDecoder();

    private static final Decoder URL_DECODER = Base64.getUrlDecoder();

    /**
     * 使用默认编码
     *
     * @param input
     * @return
     */
    public static String encodeToStr(String input) {
        return ENCODER.encodeToString(input.getBytes(CharsetUtil.CHARSET_UTF_8));
//        return new Base64().encodeToString(input.getBytes());
    }

    /**
     * @param input
     * @return
     */
    public static String encodeToStr(byte[] input) {
        return ENCODER.encodeToString(input);
//        return new Base64().encodeToString(input.getBytes());
    }

    /**
     * 使用指定编码加密
     *
     * @param input
     * @param charset
     * @return
     */
    public static String encodeToStr(String input, Charset charset) {
        return ENCODER.encodeToString(null == charset ? input.getBytes() : input.getBytes(charset));
    }

    /**
     * 加密流程
     *
     * @param input
     * @return
     */
    public static byte[] encode(byte[] input) {
        return ENCODER.encode(input);
    }

    /**
     * URL安全的加密
     *
     * @param input
     * @param charset
     * @return
     */
    public static String encodeUrlSafe(String input, Charset charset) {
        return URL_ENCODER.encodeToString(null == charset ? input.getBytes() : input.getBytes(charset));
    }

    /**
     * 使用默认UTF8解码
     *
     * @param input
     * @return
     */
    public static String decodeToStr(String input) {
//        return new String(new Base64().decode(input), charset);
        return new String(DECODER.decode(input), CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 解码Base64
     *
     * @param input
     * @param charset
     * @return
     */
    public static String decodeToStr(String input, Charset charset) {
//        return new String(new Base64().decode(input), charset);
        if (charset == null) {
            return new String(DECODER.decode(input));
        }
        return new String(DECODER.decode(input), charset);
    }

    /**
     * @param input
     * @return
     */
    public static byte[] decode(String input) {
        return DECODER.decode(input.getBytes(CharsetUtil.CHARSET_UTF_8));
    }

    /**
     * 解压到Bytes里面
     *
     * @param input
     * @param charset
     * @return
     */
    public static byte[] decode(String input, Charset charset) {
        return DECODER.decode(null == charset ? input.getBytes() : input.getBytes(charset));
    }

    /**
     * URL安全的解码
     *
     * @param input
     * @param charset
     * @return
     */
    public static String urlDecode(String input, Charset charset) {
        return new String(URL_DECODER.decode(input), charset);
    }

}
