package server;

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
public class ServletWrapper {
    private Servlet instance = null;
    private String servletClass;
    private ClassLoader loader;
    private String name;
    private ServletContainer parent = null;

    public ServletWrapper(String servletClass, ServletContainer parent) {
        this.parent = parent;
        this.servletClass = servletClass;
        try {
            loadServlet();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    public ClassLoader getLoader() {
        if (loader != null) {
            return loader;
        }
        return parent.getLoader();
    }

    public String getServletClass() {
        return servletClass;
    }

    public void setServletClass(String servletClass) {
        this.servletClass = servletClass;
    }

    public ServletContainer getParent() {
        return parent;
    }

    public void setParent(ServletContainer parent) {
        this.parent = parent;
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

    public void invoke(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (instance != null) {
            instance.service(req, resp);
        }
    }
}
