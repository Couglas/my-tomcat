package com.mytomcat;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

/**
 * 封装器
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public interface Wrapper {
    void setLoadOnStartup(int value);

    String getServletClass();

    void setServletClass(String servletClass);

    void addInitParameter(String name, String value);

    Servlet allocate() throws ServletException;

    String findInitParameter(String name);

    String[] findInitParameters();

    void load() throws ServletException;

    void removeInitParameter(String name);
}

