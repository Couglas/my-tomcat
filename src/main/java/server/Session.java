package server;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * session
 *
 * @author zhenxingchen4
 * @since 2025/5/16
 */
public class Session implements HttpSession {
    private String sessionId;
    private long creationTime;
    private boolean valid;
    private Map<String, Object> attributes = new ConcurrentHashMap<>();


    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public String getId() {
        return this.sessionId;
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int i) {

    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return this.attributes.get(s);
    }

    @Override
    public Object getValue(String s) {
        return this.attributes.get(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(this.attributes.keySet());
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String s, Object o) {
        this.attributes.put(s, o);
    }

    @Override
    public void putValue(String s, Object o) {
        this.attributes.put(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        this.attributes.remove(s);
    }

    @Override
    public void removeValue(String s) {
        this.attributes.remove(s);
    }

    @Override
    public void invalidate() {
        this.valid = false;
    }

    @Override
    public boolean isNew() {
        return false;
    }

    public void setId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
