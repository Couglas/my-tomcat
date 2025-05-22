package com.mytomcat;

/**
 * session监听器
 *
 * @author zhenxingchen4
 * @since 2025/5/21
 */
public interface SessionListener {
    void sessionEvent(SessionEvent sessionEvent);
}
