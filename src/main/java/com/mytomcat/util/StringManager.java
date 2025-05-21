package com.mytomcat.util;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * string工具类
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public class StringManager {
    private static Map<String, StringManager> managers = new ConcurrentHashMap<>();

    public StringManager(String packageName) {

    }

    public synchronized static StringManager getManager(String packageName) {
        StringManager stringManager = managers.get(packageName);
        if (stringManager == null) {
            stringManager = new StringManager(packageName);
            managers.put(packageName, stringManager);
        }
        return stringManager;
    }

    public String getString(String key) {
        if (key == null) {
            String msg = "key is null";
            throw new NullPointerException(msg);
        }

        return key;
    }

    public String getString(String key, Object[] args) {
        String str = null;
        String value = getString(key);

        try {
            Object nonNullArgs[] = args;
            for (int i = 0; i < args.length; i++) {
                if (args[i] == null) {
                    if (nonNullArgs == args) {
                        nonNullArgs = args.clone();
                    }
                    nonNullArgs[i] = "null";
                }
                str = MessageFormat.format(value, nonNullArgs);
            }
        } catch (IllegalArgumentException e) {
            StringBuffer buf = new StringBuffer();
            buf.append(value);
            for (int i = 0; i < args.length; i++) {
                buf.append(" arg[" + i + "]=" + args[i]);
            }
            str = buf.toString();

        }

        return str;
    }

    public String getString(String key, Object arg) {
        Object[] args = new Object[]{arg};
        return getString(key, args);
    }

    public String getString(String key, Object arg1, Object arg2) {
        Object[] args = new Object[]{arg1, arg2};
        return getString(key, args);
    }

    public String getString(String key, Object arg1, Object arg2, Object arg3) {
        Object[] args = new Object[]{arg1, arg2, arg3};
        return getString(key, args);
    }

    public String getString(String key, Object arg1, Object arg2, Object arg3, Object arg4) {
        Object[] args = new Object[]{arg1, arg2, arg3, arg4};
        return getString(key, args);
    }

}
