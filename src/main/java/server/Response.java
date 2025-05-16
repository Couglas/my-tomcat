package server;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import java.io.*;
import java.util.Locale;

/**
 * 响应
 *
 * @author zhenxingchen4
 * @since 2025/5/14
 */
public class Response implements ServletResponse {
    private HttpRequest request;
    private OutputStream output;
    private String characterEncoding;

    public Response(OutputStream output) {
        this.output = output;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public OutputStream getOutput() {
        return output;
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new OutputStreamWriter(output, getCharacterEncoding()), true);
    }

    @Override
    public void setCharacterEncoding(String s) {
        this.characterEncoding = s;
    }

    @Override
    public void setContentLength(int i) {

    }

    @Override
    public void setContentLengthLong(long l) {

    }

    @Override
    public void setContentType(String s) {

    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
