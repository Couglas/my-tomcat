package com.mytomcat.connector.http;

import com.mytomcat.Connector;
import com.mytomcat.Context;
import com.mytomcat.Request;
import com.mytomcat.Response;
import com.mytomcat.util.CookieTools;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * http响应
 *
 * @author zhenxingchen4
 * @since 2025/5/16
 */
public class HttpResponseImpl implements HttpServletResponse, Response {
    private HttpRequestImpl request;
    private OutputStream output;
    private PrintWriter writer;
    private String contentType = null;
    private int contentLength = -1;
    private String charset = null;
    private String characterEncoding = "UTF-8";
    private String protocol = "HTTP/1.1";
    Map<String, String> headers = new ConcurrentHashMap<>();
    private String message = getStatusMessage(HttpServletResponse.SC_OK);
    private int status = HttpServletResponse.SC_OK;
    private List<Cookie> cookies = new ArrayList<>();

    public HttpResponseImpl() {
    }

    public HttpResponseImpl(OutputStream output) {
        this.output = output;
    }

    public void setRequest(HttpRequestImpl request) {
        this.request = request;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public OutputStream getOutput() {
        return this.output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }

    @Override
    public Connector getConnector() {
        return null;
    }

    @Override
    public void setConnector(Connector connector) {

    }

    @Override
    public int getContentCount() {
        return 0;
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public void setContext(Context context) {

    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public Request getRequest() {
        return null;
    }

    @Override
    public void setRequest(Request request) {

    }

    @Override
    public ServletResponse getResponse() {
        return null;
    }

    @Override
    public OutputStream getStream() {
        return null;
    }

    @Override
    public void setStream(OutputStream stream) {
        this.output = output;
    }

    @Override
    public void setError() {

    }

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public ServletOutputStream createOutputStream() throws IOException {
        return null;
    }

    public void finishResponse() {
        try {
            this.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getStatusMessage(int status) {
        switch (status) {
            case SC_OK:
                return "OK";
            case SC_ACCEPTED:
                return "Accepted";
            case SC_BAD_GATEWAY:
                return "Bad Gateway";
            case SC_CONTINUE:
                return "Continue";
            case SC_FORBIDDEN:
                return "Forbidden";
            case SC_INTERNAL_SERVER_ERROR:
                return "Internal Server Error";
            case SC_METHOD_NOT_ALLOWED:
                return "Method Not Allowed";
            case SC_NOT_FOUND:
                return "Not Found";
            case SC_NOT_IMPLEMENTED:
                return "Not Implemented";
            case SC_REQUEST_URI_TOO_LONG:
                return "com.mytomcat.Request Uri Too Long";
            case SC_SERVICE_UNAVAILABLE:
                return "Service Unavailable";
            case SC_UNAUTHORIZED:
                return "Unauthorized";
            default:
                return "Http com.mytomcat.Response Status: " + status;
        }
    }

    public void sendHeaders() throws IOException {
        PrintWriter outputWriter = getWriter();
        outputWriter.print(this.getProtocol());
        outputWriter.print(" ");
        outputWriter.println(status);
        if (message != null) {
            outputWriter.print(" ");
            outputWriter.print(message);
        }
        outputWriter.print("\r\n");
        if (getContentType() != null) {
            outputWriter.print("Content-type: " + getContentType() + "\r\n");
        }
        if (getContentLength() > 0) {
            outputWriter.print("Content-length: " + getContentLength() + "\r\n");
        }
        Iterator<String> names = headers.keySet().iterator();
        while (names.hasNext()) {
            String name = names.next();
            String value = headers.get(name);
            outputWriter.print(name);
            outputWriter.print(": ");
            outputWriter.print(value);
            outputWriter.print("\r\n");
        }
        HttpSession session = this.request.getSession(false);
        if (session != null) {
            Cookie cookie = new Cookie(DefaultHeaders.JSESSIONID_NAME, session.getId());
            cookie.setMaxAge(-1);
            addCookie(cookie);
        }

        synchronized (cookies) {
            Iterator<Cookie> cookieIterator = cookies.iterator();
            while (cookieIterator.hasNext()) {
                Cookie cookie = cookieIterator.next();
                outputWriter.print(CookieTools.getCookieHeaderName(cookie));
                outputWriter.print(": ");
                StringBuffer sb = new StringBuffer();
                CookieTools.getCookieHeaderValue(cookie, sb);
                System.out.println("set cookie jsessionid: " + sb);
                outputWriter.print(sb);
                outputWriter.print("\r\n");
            }
        }

        outputWriter.print("\r\n");
        outputWriter.flush();
    }

    @Override
    public void addCookie(Cookie cookie) {
        synchronized (cookies) {
            Iterator<Cookie> items = cookies.iterator();
            while (items.hasNext()) {
                Cookie c = items.next();
                if (c.getName().equals(cookie.getName())) {
                    items.remove();
                }
            }
            cookies.add(cookie);
        }
    }

    @Override
    public boolean containsHeader(String s) {
        return headers.get(s) != null;
    }

    @Override
    public String encodeURL(String s) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String s) {
        return null;
    }

    @Override
    public String encodeUrl(String s) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String s) {
        return null;
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {

    }

    @Override
    public void sendRedirect(String s) throws IOException {

    }

    @Override
    public void setDateHeader(String s, long l) {

    }

    @Override
    public void addDateHeader(String s, long l) {

    }

    @Override
    public void setHeader(String s, String s1) {
        headers.put(s, s1);
        if (s.equalsIgnoreCase(DefaultHeaders.CONTENT_LENGTH_NAME)) {
            setContentLength(Integer.parseInt(s1));
        }
        if (s.equalsIgnoreCase(DefaultHeaders.CONTENT_TYPE_NAME)) {
            setContentType(s1);
        }
    }

    @Override
    public void addHeader(String s, String s1) {
        headers.put(s, s1);
        if (s.equalsIgnoreCase(DefaultHeaders.CONTENT_LENGTH_NAME)) {
            setContentLength(Integer.parseInt(s1));
        }
        if (s.equalsIgnoreCase(DefaultHeaders.CONTENT_TYPE_NAME)) {
            setContentType(s1);
        }
    }

    @Override
    public void setIntHeader(String s, int i) {

    }

    @Override
    public void addIntHeader(String s, int i) {

    }

    @Override
    public void setStatus(int i) {
        this.status = i;
        this.message = this.getStatusMessage(i);
    }

    @Override
    public void setStatus(int i, String s) {

    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getHeader(String s) {
        return headers.get(s);
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public PrintWriter getReporter() {
        return null;
    }

    @Override
    public void recycle() {

    }

    @Override
    public int getContentLength() {
        return this.contentLength;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(output, getCharacterEncoding()), true);
        }
        return this.writer;
    }

    @Override
    public void setCharacterEncoding(String s) {
        this.characterEncoding = s;
    }

    @Override
    public void setContentLength(int i) {
        this.contentLength = i;
    }

    @Override
    public void setContentLengthLong(long l) {

    }

    @Override
    public void setContentType(String s) {
        this.contentType = s;
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
    public void sendAcknowledgement() throws IOException {

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
