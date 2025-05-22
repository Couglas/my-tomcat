package com.mytomcat;

import java.util.EventObject;

/**
 * session事件
 *
 * @author zhenxingchen4
 * @since 2025/5/21
 */
public final class SessionEvent extends EventObject {
    private Session session;
    private String type;
    private Object data;

    public SessionEvent(Session session, String type, Object data) {
        super(session);
        this.session = session;
        this.type = type;
        this.data = data;
    }

    public Session getSession() {
        return session;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "SessionEvent[' " + getSession() + "', '" + getType() + "']";
    }
}
