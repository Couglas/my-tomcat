package com.mytomcat.connector.http;

/**
 * http请求头
 *
 * @author zhenxingchen4
 * @since 2025/5/16
 */
public class HttpHeader {
    private static final int INITIAL_NAME_SIZE = 64;
    private static final int INITIAL_VALUE_SIZE = 512;
    private static final int MAX_NAME_SIZE = 128;
    private static final int MAX_VALUE_SIZE = 1024;
    public char[] name;
    public int nameEnd;
    public char[] value;
    public int valueEnd;
    protected int hashCode = 0;

    public HttpHeader() {
        this(new char[INITIAL_NAME_SIZE], 0, new char[INITIAL_VALUE_SIZE], 0);
    }

    public HttpHeader(char[] name, int nameEnd, char[] value, int valueEnd) {
        this.name = name;
        this.nameEnd = nameEnd;
        this.value = value;
        this.valueEnd = valueEnd;
    }

    public HttpHeader(String name, String value) {
        this.name = name.toLowerCase().toCharArray();
        this.nameEnd = name.length();
        this.value = value.toLowerCase().toCharArray();
        this.valueEnd = value.length();
    }

    public void recycle() {
        nameEnd = 0;
        valueEnd = 0;
        hashCode = 0;
    }

}
