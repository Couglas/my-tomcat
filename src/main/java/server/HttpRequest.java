package server;

import javax.servlet.*;
import javax.servlet.ServletContext;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * http请求
 *
 * @author zhenxingchen4
 * @since 2025/5/16
 */
public class HttpRequest implements HttpServletRequest {
    private InputStream input;
    private SocketInputStream sis;
    private String uri;
    private InetAddress address;
    private int port;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String[]> parameters = new ConcurrentHashMap<>();
    private HttpRequestLine requestLine = new HttpRequestLine();
    private String queryString;
    private boolean parsed = false;
    private HttpSession session;
    private String sessionid;
    private SessionFacade sessionFacade;
    private Cookie[] cookies;
    private HttpServletResponse response;

    public HttpRequest(InputStream input) {
        this.input = input;
        this.sis = new SocketInputStream(this.input, 2048);
    }

    public void setStream(InputStream input) {
        this.input = input;
        this.sis = new SocketInputStream(this.input, 2048);
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public void parse(Socket socket) {
        try {
            parseConnection(socket);
            this.sis.readRequestLine(requestLine);
            parseRequestLine();
            parseHeaders();
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }

    private void parseRequestLine() {
        int pos = requestLine.indexOf("?");
        if (pos >= 0) {
            queryString = new String(requestLine.uri, pos + 1, requestLine.uriEnd - pos - 1);
            uri = new String(requestLine.uri, 0, pos);
        } else {
            queryString = null;
            uri = new String(requestLine.uri, 0, requestLine.uriEnd);
        }

        String jsessionid = ";" + DefaultHeaders.JSESSIONID_NAME + "=";
        int semicolon = uri.indexOf(jsessionid);
        if (semicolon >= 0) {
            sessionid = uri.substring(semicolon + jsessionid.length());
            uri = uri.substring(0, semicolon);
        }
    }

    private void parseParameters() {
        String encoding = getCharacterEncoding();
        System.out.println("encoding: " + encoding);
        if (encoding == null) {
            encoding = "ISO-8859-1";
        }

        String qs = getQueryString();
        System.out.println("qs: " + qs);
        if (qs != null) {
            byte[] bytes = new byte[qs.length()];
            try {
                bytes = qs.getBytes(encoding);
                parseParameters(this.parameters, bytes, encoding);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        String contentType = getContentType();
        if (contentType == null) {
            contentType = "";
        }
        int semicolon = contentType.indexOf(";");
        if (semicolon >= 0) {
            contentType = contentType.substring(0, semicolon).trim();
        } else {
            contentType = contentType.trim();
        }

        if ("POST".equals(getMethod()) && getContentLength() > 0 && "application/x-www-form-urlencoded".equals(contentType)) {
            try {
                int max = getContentLength();
                int len = 0;
                byte[] buf = new byte[max];
                ServletInputStream is = getInputStream();
                while (len < max) {
                    int next = is.read(buf, len, max - len);
                    if (next < 0) {
                        break;
                    }
                    len += next;
                }
                is.close();
                if (len < max) {
                    throw new RuntimeException("content length mismatch");
                }
                parseParameters(this.parameters, buf, encoding);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseParameters(Map<String, String[]> map, byte[] data, String encoding) throws UnsupportedEncodingException {
        if (parsed) {
            return;
        }
        System.out.println("data: " + data);
        if (data != null && data.length > 0) {
            int pos = 0;
            int ix = 0;
            int ox = 0;
            String key = null;
            String value = null;
            while (ix < data.length) {
                byte c = data[ix++];
                switch ((char) c) {
                    case '&':
                        value = new String(data, 0, ox, encoding);
                        if (key != null) {
                            putMapEntry(map, key, value);
                            key = null;
                        }
                        ox = 0;
                        break;
                    case '=':
                        key = new String(data, 0, ox, encoding);
                        ox = 0;
                        break;
                    case '+':
                        data[ox++] = (byte) ' ';
                        break;
                    case '%':
                        data[ox++] = (byte)((convertHexDigit(data[ix++]) << 4) + convertHexDigit(data[ix++]));
                        break;
                    default:
                        data[ox++] = c;
                }
            }
            if (key != null) {
                value = new String(data, 0, ox, encoding);
                putMapEntry(map, key, value);
            }
        }
        parsed = true;
    }

    private byte convertHexDigit(byte b) {
        if (b >= '0' && b <= '9') {
            return (byte) (b - '0');
        }
        if (b >= 'a' && b <= 'f') {
            return (byte) (b - 'a' + 10);
        }
        if (b >= 'A' && b <= 'F') {
            return (byte) (b - 'A' + 10);
        }
        return 0;
    }

    private void putMapEntry(Map<String, String[]> map, String key, String value) {
        String[] newValues = null;
        String[] oldValues = map.get(key);
        if (oldValues == null) {
            newValues = new String[1];
            newValues[0] = value;
        } else {
            newValues = new String[oldValues.length + 1];
            System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
            newValues[oldValues.length] = value;
        }
        map.put(key, newValues);
    }

    private void parseHeaders() throws IOException, ServletException {
        while (true) {
            HttpHeader header = new HttpHeader();
            sis.readHeader(header);
            if (header.nameEnd == 0) {
                if (header.valueEnd == 0) {
                    return;
                } else {
                    throw new ServletException("parse header error");
                }
            }
            String name = new String(header.name, 0, header.nameEnd);
            String value = new String(header.value, 0, header.valueEnd);
            name = name.toLowerCase();
            switch (name) {
                case DefaultHeaders.ACCEPT_LANGUAGE_NAME:
                case DefaultHeaders.CONTENT_LENGTH_NAME:
                case DefaultHeaders.CONTENT_TYPE_NAME:
                case DefaultHeaders.HOST_NAME:
                case DefaultHeaders.TRANSFER_ENCODING_NAME:
                    headers.put(name, value);
                    break;
                case DefaultHeaders.COOKIE_NAME:
                    headers.put(name, value);
                    this.cookies = parseCookieHeader(value);
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("jsessionid")) {
                            this.sessionid = cookie.getValue();
                        }
                    }
                    break;
                case DefaultHeaders.CONNECTION_NAME:
                    headers.put(name, value);
                    if (value.equals("close")) {
                        response.setHeader("Connection", "close");
                    }
                    break;
            }
        }
    }

    private Cookie[] parseCookieHeader(String header) {
        if (header == null || header.isEmpty()) {
            return new Cookie[0];
        }
        ArrayList<Cookie> cookieList = new ArrayList<>();
        while (!header.isEmpty()) {
            int semicolon = header.indexOf(";");
            if (semicolon < 0) {
                semicolon = header.length();
            }
            if (semicolon == 0) {
                break;
            }

            String token = header.substring(0, semicolon);
            if (semicolon < header.length()) {
                header = header.substring(semicolon + 1);
            } else {
                header = "";
            }
            int equals = token.indexOf("=");
            if (equals > 0) {
                String name = token.substring(0, equals).trim();
                String value = token.substring(equals + 1).trim();
                cookieList.add(new Cookie(name, value));
            }
        }
        return cookieList.toArray(new Cookie[0]);
    }

    private void parseConnection(Socket socket) throws IOException {
        this.address = socket.getInetAddress();
        this.port = socket.getPort();
    }

    public String getUri() {
        return this.uri;
    }


    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return this.cookies;
    }

    @Override
    public long getDateHeader(String s) {
        return 0;
    }

    @Override
    public String getHeader(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return null;
    }

    @Override
    public int getIntHeader(String s) {
        return 0;
    }

    @Override
    public String getMethod() {
        return new String(this.requestLine.method, 0, this.requestLine.methodEnd);
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getQueryString() {
        return this.queryString;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return null;
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean b) {
        if (sessionFacade != null) {
            return sessionFacade;
        }
        if (sessionid != null) {
            session = HttpConnector.sessions.get(sessionid);
            if (session == null) {
                session = HttpConnector.createSession();
            }
            sessionFacade = new SessionFacade(session);
            return sessionFacade;
        }

        session = HttpConnector.createSession();
        sessionFacade = new SessionFacade(session);
        sessionid = session.getId();
        return sessionFacade;
    }

    @Override
    public HttpSession getSession() {
        return this.sessionFacade;
    }

    public String getSessionId() {
        return this.sessionid;
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String s, String s1) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return headers.get(DefaultHeaders.TRANSFER_ENCODING_NAME);
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return Integer.parseInt(headers.get(DefaultHeaders.CONTENT_LENGTH_NAME));
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return headers.get(DefaultHeaders.CONTENT_TYPE_NAME);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return this.sis;
    }

    @Override
    public String getParameter(String s) {
        parseParameters();
        String[] values = parameters.get(s);
        if (values != null) {
            return values[0];
        }
        return null;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        parseParameters();
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String s) {
        parseParameters();
        return parameters.get(s);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        parseParameters();
        return this.parameters;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    public String getRealPath(String s) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }
}
