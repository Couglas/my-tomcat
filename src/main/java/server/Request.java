package server;

import java.io.IOException;
import java.io.InputStream;

/**
 * 请求
 *
 * @author zhenxingchen4
 * @since 2025/5/14
 */
public class Request {
    private InputStream input;
    private String uri;

    public Request(InputStream input) {
        this.input = input;
    }

    public String getUri() {
        return uri;
    }

    public void parse() {
        StringBuffer request = new StringBuffer(2048);
        int i;
        byte[] buffer = new byte[2048];
        try {
            i = input.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            i = -1;
        }
        for (int j = 0; j < i; j++) {
            request.append((char) buffer[j]);
        }
        uri = parseUri(request.toString());
    }

    private String parseUri(String req) {
        int index1, index2;
        index1 = req.indexOf(' ');
        if (index1 != -1) {
            index2 = req.indexOf(' ', index1 + 1);
            if (index2 > index1) {
                return req.substring(index1 + 1, index2);
            }
        }
        return null;
    }


}
