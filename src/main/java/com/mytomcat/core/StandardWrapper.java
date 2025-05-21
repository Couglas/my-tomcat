package com.mytomcat.core;

import com.mytomcat.Container;
import com.mytomcat.Wrapper;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * servlet包装类
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public class StandardWrapper extends ContainerBase implements Wrapper {
    private Servlet instance = null;
    private String servletClass;

    public StandardWrapper(String servletClass, StandardContext parent) {
        this.parent = parent;
        this.servletClass = servletClass;
        try {
            loadServlet();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getInfo() {
        return "My Servlet com.mytomcat.Wrapper, version 0.1";
    }

    @Override
    public void setLoadOnStartup(int value) {

    }

    public String getServletClass() {
        return servletClass;
    }

    public void setServletClass(String servletClass) {
        this.servletClass = servletClass;
    }

    @Override
    public void addInitParameter(String name, String value) {

    }

    @Override
    public Servlet allocate() throws ServletException {
        return null;
    }

    @Override
    public String findInitParameter(String name) {
        return null;
    }

    @Override
    public String[] findInitParameters() {
        return new String[0];
    }

    @Override
    public void load() throws ServletException {

    }

    @Override
    public void removeInitParameter(String name) {

    }

    public Servlet getServlet() {
        return instance;
    }

    private Servlet loadServlet() throws ServletException {
        if (instance != null) {
            return instance;
        }
        Servlet servlet = null;
        if (servletClass == null) {
            throw new ServletException("Servlet class not specified");
        }
        ClassLoader classLoader = getLoader();
        Class<?> clazz = null;
        try {
            if (classLoader != null) {
                clazz = classLoader.loadClass(servletClass);
            }
        } catch (ClassNotFoundException e) {
            throw new ServletException("Servlet class not found");
        }

        try {
            servlet = (Servlet) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServletException("Failed to instantiate servlet");
        }

        try {
            servlet.init(null);
        } catch (ServletException e) {
            throw new ServletException("Failed initialize servlet");
        }
        instance = servlet;
        return servlet;
    }

    @Override
    public void invoke(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (instance != null) {
            instance.service(req, resp);
        }
    }

    @Override
    public void addChild(Container child) {

    }

    @Override
    public Container findChild(String name) {
        return null;
    }

    @Override
    public Container[] findChildren() {
        return null;
    }

    @Override
    public void removeChild(Container child) {

    }
}
