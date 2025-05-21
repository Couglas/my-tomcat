package com.mytomcat;

import javax.servlet.ServletContext;

/**
 * 上下文
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public interface Context extends Container {
    String RELOAD_EVENT = "reload";

    String getDisplayName();

    void setDisplayName(String displayName);

    String getDocBase();

    void setDocBase(String docBase);

    String getPath();

    void setPath(String path);

    ServletContext getServletContext();

    int getSessionTimeout();

    void setSessionTimeout(int timeout);

    String getWrapperClass();

    void setWrapperClass(String wrapperClass);

    Wrapper createWrapper();

    String findServletMapping(String pattern);

    String[] findServletMappings();

    void reload();
}
