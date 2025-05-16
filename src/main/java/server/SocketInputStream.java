package server;

import java.io.IOException;
import java.io.InputStream;

/**
 * socket输入流
 * <p>
 * 从 InputStream 里读一个个的字节，放到 buf 里，buf 有长度限制，读到尾后就从头再来。
 * 然后从 buf 里一个字节一个字节地判断，pos 变量代表当前读取的位置，根据协议规定的分隔符解析到 requestLine 和 header 里
 *
 * @author zhenxingchen4
 * @since 2025/5/16
 */
public class SocketInputStream extends InputStream {
    private static final byte CR = '\r';
    private static final byte LF = '\n';
    private static final byte SP = ' ';
    private static final byte HT = '\t';
    private static final byte COLON = ':';
    private static final int LC_OFFSET = 'A' - 'a';
    private byte buf[];
    private int count;
    private int pos;
    private InputStream is;

    public SocketInputStream(InputStream is, int bufferSize) {
        this.is = is;
        buf = new byte[bufferSize];
    }

    public void readRequestLine(HttpRequestLine requestLine) throws IOException {
        // 跳过空行
        int chr = 0;
        do {
            chr = read();
        } while ((chr == CR) || (chr == LF));
        pos--;
        // 解析method，以空格结束
        int maxRead = requestLine.method.length;
        int readStart = pos;
        int readCount = 0;
        boolean space = false;
        while (!space) {
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException("read method error");
                }
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == SP) {
                space = true;
            }
            requestLine.method[readCount] = (char) buf[pos];
            readCount++;
            pos++;
        }
        requestLine.methodEnd = readCount - 1;
        // 解析uri，以空格结束
        maxRead = requestLine.uri.length;
        readStart = pos;
        readCount = 0;
        space = false;
        while (!space) {
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException("read uri error");
                }
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == SP) {
                space = true;
            }
            requestLine.uri[readCount] = (char) buf[pos];
            readCount++;
            pos++;
        }
        requestLine.uriEnd = readCount - 1;
        // 解析protocol，以eol结尾
        maxRead = requestLine.protocol.length;
        readStart = pos;
        readCount = 0;
        boolean eol = false;
        while (!eol) {
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException("read protocol error");
                }
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == CR) {
                // 跳过
            } else if (buf[pos] == LF) {
                eol = true;
            } else {
                requestLine.protocol[readCount] = (char) buf[pos];
                readCount++;
            }
            pos++;
        }
        requestLine.protocolEnd = readCount;
    }

    public void readHeader(HttpHeader header) throws IOException {
        int ch = read();
        if (ch == CR || ch == LF) {
            if (ch == CR) {
                read();
            }
            header.nameEnd = 0;
            header.valueEnd = 0;
            return;
        } else {
            pos--;
        }
        // 读header name
        int maxRaed = header.name.length;
        int readStart = pos;
        int readCount = 0;
        boolean colon = false;
        while (!colon) {
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException("read header name error");
                }
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == COLON) {
                colon = true;
            }
            char val = (char) buf[pos];
            if (val >= 'A' && val <= 'Z') {
                val = (char) (val - LC_OFFSET);
            }
            header.name[readCount] = val;
            readCount++;
            pos++;
        }
        header.nameEnd = readCount - 1;
        // 解析header value
        maxRaed = header.value.length;
        readStart = pos;
        readCount = 0;
        int crPos = -2;
        boolean eol = false;
        boolean validLine = true;
        while (validLine) {
            boolean space = true;
            while (space) {
                if (pos >= count) {
                    int val = read();
                    if (val == -1) {
                        throw new IOException("read header value error");
                    }
                    pos = 0;
                    readStart = 0;
                }
                if (buf[pos] == SP || buf[pos] == HT) {
                    pos++;
                } else {
                    space = false;
                }
            }
            while (!eol) {
                if (pos >= count) {
                    int val = read();
                    if (val == -1) {
                        throw new IOException("read header value error ");
                    }
                    pos = 0;
                    readStart = 0;
                }
                if (buf[pos] == CR) {

                } else if (buf[pos] == LF) {
                    eol = true;
                } else {
                    int c = buf[pos] & 0xff;
                    header.value[readCount] = (char) c;
                    readCount++;
                }
                pos++;
            }
            int nextCh = read();
            if (nextCh != SP && nextCh != HT) {
                pos--;
                validLine = false;
            } else {
                eol = false;
                header.value[readCount] = ' ';
                readCount++;
            }
        }
        header.valueEnd = readCount;
    }

    @Override
    public int read() throws IOException {
        if (pos >= count) {
            fill();
            if (pos >= count) {
                return -1;
            }
        }
        return buf[pos++] & 0xff;
    }

    private void fill() throws IOException {
        pos = 0;
        count = 0;
        int nRead = is.read(buf, 0, buf.length);
        if (nRead > 0) {
            count = nRead;
        }
    }

    @Override
    public int available() throws IOException {
        return (count - pos) + is.available();
    }

    @Override
    public void close() throws IOException {
        if (is == null) {
            return;
        }
        is.close();
        is = null;
        buf = null;
    }
}
