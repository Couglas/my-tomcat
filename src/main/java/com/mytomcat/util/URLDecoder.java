package com.mytomcat.util;

import java.io.UnsupportedEncodingException;

/**
 * URL解码器
 *
 * @author zhenxingchen4
 * @since 2025/5/21
 */
public class URLDecoder {
    public static String URLDecode(String str) {
        return URLDecode(str, null);
    }

    public static String URLDecode(String str, String enc) {
        if (str == null) {
            return null;
        }
        byte[] bytes = str.getBytes();
        return URLDecode(bytes, enc);
    }

    public static String URLDecode(byte[] bytes) {
        return URLDecode(bytes, null);
    }

    public static String URLDecode(byte[] bytes, String enc) {
        if (bytes == null) {
            return null;
        }

        int len = bytes.length;
        int ix = 0;
        int ox = 0;
        while (ix < len) {
            byte b = bytes[ix++];
            if (b == '+') {
                b = (byte) ' ';
            } else if (b == '%') {
                b = (byte) ((convertHexDigit(bytes[ix++]) << 4) + convertHexDigit(bytes[ix++]));
            }
            bytes[ox++] = b;
        }
        if (enc != null) {
            try {
                return new String(bytes, 0, ox, enc);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return new String(bytes, 0, ox);
    }

    private static byte convertHexDigit(byte b) {
        if ((b >= '0') && (b <= '9')) {
            return (byte) (b - '0');
        }
        if ((b >= 'a') && (b <= 'f')) {
            return (byte) (b - 'a' + 10);
        }
        if ((b >= 'A') && (b <= 'F')) {
            return (byte) (b - 'A' + 10);
        }
        return 0;
    }
}
