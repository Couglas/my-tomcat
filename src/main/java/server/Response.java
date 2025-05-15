package server;

import java.io.*;

/**
 * 响应
 *
 * @author zhenxingchen4
 * @since 2025/5/14
 */
public class Response {
    private Request request;
    private OutputStream output;

    public Response(OutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public OutputStream getOutput() {
        return output;
    }
}
