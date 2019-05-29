package com.lindonge.core.thirdparty.shorturl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.IntStream;

public class ShortUrlUtil {

    static char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B',
            'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z'};

    static char[] DIGITS_RAND = {'k', '4', 'j', 'F', 'E', 'z', 'r', 'O', 'e', '1', 'i', '8', '9', 'l', 'q', 'T', 'A', '5', 'R', 't', 'h', 'b', 'D', 'W', 'd', 'Y', 'I', 'X', '0', 'K', 'o', 'm', '6', 'L', 's', 'x', 'c', '7', 'a', 'Q', 'C', 'P', 'G', 'M', '3', 'f', 'V', 'n', 'H', 'J', 'Z', 'N', 'U', 'y', 'u', 'g', 'B', 'S', 'w', 'v', 'p', '2'};

    private static final int BINARY = 0x2;

    private static final int NUMBER_61 = 0x0000003d;

    /**
     * 可以自定义生成 MD5 加密字符传前的混合 KEY
     */
    private static String encryptKey = "kylexyz";

    /**
     * 算法一：自增逻辑缩短
     * Long转62进制
     * 进制换算工具：https://tool.lu/hexconvert/
     * 基数最好从1万起，1万大概是3位数的短址
     * 1、字符打乱，显示不会有序
     * 2、为了适配自定义短址，可以在发生冲突时，把冲突短址的自增ID拿出来算短址
     * @param seq
     * @return
     */
    public static String to62RadixString(long seq) {
        StringBuilder sBuilder = new StringBuilder();
        while (true) {
            int remainder = (int) (seq % 62);
            sBuilder.append(DIGITS_RAND[remainder]);
            seq = seq / 62;
            if (seq == 0) {
                break;
            }
        }
        return sBuilder.toString();
    }

    /**
     * 算法二：Md5缩短
     * 指定长度转换短址
     *
     * @param longUrl   长网址
     * @param urlLength 生成长度，只能为5或者6
     * @return
     */
    public static String shorten(String longUrl, int urlLength) {
        if (urlLength < 0 || urlLength > 6) {
            throw new IllegalArgumentException("the length of url must be between 0 and 6");
        }
        String md5Hex = DigestUtils.md5Hex(encryptKey + longUrl);
        // 6 digit binary can indicate 62 letter & number from 0-9a-zA-Z
        int binaryLength = urlLength * 6;
        long binaryLengthFixer = Long.valueOf(StringUtils.repeat("1", binaryLength), BINARY);
        for (int i = 0; i < 4; i++) {
            String subString = StringUtils.substring(md5Hex, i * 8, (i + 1) * 8);
            subString = Long.toBinaryString(Long.valueOf(subString, 16) & binaryLengthFixer);
            subString = StringUtils.leftPad(subString, binaryLength, "0");
            StringBuilder sbBuilder = new StringBuilder();
            for (int j = 0; j < urlLength; j++) {
                String subString2 = StringUtils.substring(subString, j * 6, (j + 1) * 6);
                int charIndex = Integer.valueOf(subString2, BINARY) & NUMBER_61;
                sbBuilder.append(DIGITS[charIndex]);
            }
            return sbBuilder.toString();
        }
        // if all 4 possibilities are already exists
        return null;
    }

    public static void main(String[] args) {
        String sLongUrl = "http://www.young-sun.com"; // 3BD768E58042156E54626860E241E999

        IntStream.range(1, 10).forEach(i -> {

            String shortUrl = to62RadixString(i + 10000000);
            System.out.println("算法一：[" + i + "] is " + shortUrl);
            System.out.println("算法二：[" + i + "] is " + shorten(sLongUrl, 5));
            System.out.println("----------------------");
        });


    }
}
