package server;

/**
 * http请求首行
 * <p>
 * eg: GET /hello HTTP/1.1
 *
 * @author zhenxingchen4
 * @since 2025/5/16
 */
public class HttpRequestLine {
    public static final int INITIAL_METHOD_SIZE = 8;
    public static final int INITIAL_URI_SIZE = 128;
    public static final int INITIAL_PROTOCOL_SIZE = 8;
    public static final int MAX_METHOD_SIZE = 32;
    public static final int MAX_URI_SIZE = 2048;
    public static final int MAX_PROTOCOL_SIZE = 32;

    public char[] method;
    public int methodEnd;
    public char[] uri;
    public int uriEnd;
    public char[] protocol;
    public int protocolEnd;

    public HttpRequestLine() {
        this(new char[INITIAL_METHOD_SIZE], 0,
                new char[INITIAL_URI_SIZE], 0,
                new char[INITIAL_PROTOCOL_SIZE], 0);
    }

    public HttpRequestLine(char[] method, int methodEnd, char[] uri, int uriEnd, char[] protocol, int protocolEnd) {
        this.method = method;
        this.methodEnd = methodEnd;
        this.uri = uri;
        this.uriEnd = uriEnd;
        this.protocol = protocol;
        this.protocolEnd = protocolEnd;
    }

    public void recycle() {
        this.methodEnd = 0;
        this.uriEnd = 0;
        this.protocolEnd = 0;
    }

    public int indexOf(String str) {
        return indexOf(str.toCharArray(), str.length());
    }

    public int indexOf(char[] buf) {
        return indexOf(buf, buf.length);
    }

    public int indexOf(char[] buf, int end) {
        char c = buf[0];
        int pos = 0;
        while (pos < uriEnd) {
            pos = indexOf(c, pos);
            if (pos == -1) {
                return -1;
            }
            if ((uriEnd - pos) < end) {
                return -1;
            }
            for (int i = 0; i < end; i++) {
                if (uri[i + pos] != buf[i]) {
                    break;
                }
                if (i == (end - 1)) {
                    return pos;
                }
            }
            pos++;
        }
        return -1;
    }

    public int indexOf(char c, int start) {
        for (int i = start; i < uriEnd; i++) {
            if (uri[i] == c) {
                return i;
            }
        }
        return -1;
    }
}
