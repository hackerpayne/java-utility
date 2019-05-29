package com.lindonge.core.encode;

import com.lindonge.core.util.StringUtils;

import java.io.ByteArrayOutputStream;

/**
 * Base62编码：http://iffiffj.iteye.com/blog/618713
 * Base64：5L2G5L2/6b6Z5Z+O6aOe5bCG5ZyoIOS4jeaVmeiDoemprOW6pumYtOWxsQ==
 * Base62：LpaUdHtXIzYCI3huilByODItxjcJPHjKgoDgrV7VtukwXT9kKbscJHWmFV
 */
public class Base62 {

    private static char[] encodes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .toCharArray();

    private static byte[] decodes = new byte[256];

    static {
        for (int i = 0; i < encodes.length; i++) {
            decodes[encodes[i]] = (byte) i;
        }
    }

    /**
     * 解密Base62
     *
     * @param input
     * @return
     */
    public static String encodeBase62(String input) {
        return encodeBase62(StringUtils.bytes(input));
    }

    public static String encodeBase62(byte[] data) {
        StringBuffer sb = new StringBuffer(data.length * 2);
        int pos = 0, val = 0;
        for (int i = 0; i < data.length; i++) {
            val = (val << 8) | (data[i] & 0xFF);
            pos += 8;
            while (pos > 5) {
                char c = encodes[val >> (pos -= 6)];
                sb.append(
                /**/c == 'i' ? "ia" :
                /**/c == '+' ? "ib" :
                /**/c == '/' ? "ic" : String.valueOf(c));
                val &= ((1 << pos) - 1);
            }
        }
        if (pos > 0) {
            char c = encodes[val << (6 - pos)];
            sb.append(
            /**/c == 'i' ? "ia" :
            /**/c == '+' ? "ib" :
            /**/c == '/' ? "ic" : String.valueOf(c));
        }
        return sb.toString();
    }

    public static String decodeBase62Str(String str) {
        return StringUtils.str(decodeBase62(str), CharsetUtil.UTF_8);
    }

    public static byte[] decodeBase62(String str) {
        if (str == null) {
            return null;
        }
        char[] data = str.toCharArray();

        return decodeBase62(data);
    }

    public static byte[] decodeBase62(char[] data) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        int pos = 0, val = 0;
        for (int i = 0; i < data.length; i++) {
            char c = data[i];
            if (c == 'i') {
                c = data[++i];
                c =
                /**/c == 'a' ? 'i' :
                /**/c == 'b' ? '+' :
                /**/c == 'c' ? '/' : data[--i];
            }
            val = (val << 6) | decodes[c];
            pos += 6;
            while (pos > 7) {
                baos.write(val >> (pos -= 8));
                val &= ((1 << pos) - 1);
            }
        }
        return baos.toByteArray();
    }

}
