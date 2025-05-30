package com.mytomcat;

import javax.servlet.http.HttpSession;

/**
 * session
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public interface Session {
    String SESSION_CREATED_EVENT = "createSession";
    String SESSION_DESTROYED_EVENT = "destroySession";

    long getCreationTime();

    void setCreationTime(long time);

    String getId();

    void setId(String id);

    String getInfo();

    long getLastAccessedTime();

    int getMaxInactiveInterval();

    void setMaxInactiveInterval(int interval);

    void setNew(boolean isNew);

    HttpSession getSession();

    void setValid(boolean isValid);

    boolean isValid();

    void access();

    void expire();

    void recycle();


}
