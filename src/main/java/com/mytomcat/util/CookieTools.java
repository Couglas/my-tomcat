package com.mytomcat.util;

import javax.servlet.http.Cookie;

/**
 * cookie工具类
 *
 * @author zhenxingchen4
 * @since 2025/5/19
 */
public class CookieTools {
    private static final String tspecials = "()<>@,;:\\\"/[]?={} \t";
    public static String getCookieHeaderName(Cookie cookie) {
        return "Set-Cookie";
    }

    public static void getCookieHeaderValue(Cookie cookie, StringBuffer buffer) {
        String name = cookie.getName();
        if (name == null || name.isEmpty()) {
            name = "";
        }
        String value = cookie.getValue();
        if (value == null || value.isEmpty()) {
            value = "";
        }
        buffer.append(name);
        buffer.append("=");
        buffer.append(value);
    }

    public static void maybeQuote(int version, StringBuffer buffer, String value) {
        if (version == 0 || isToken(value)) {
            buffer.append(value);
        } else {
            buffer.append('"');
            buffer.append(value);
            buffer.append('"');
        }
    }

    private static boolean isToken(String value) {
        int len = value.length();
        for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if (c < 0x20 || c >= 0x7f || tspecials.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }
}
