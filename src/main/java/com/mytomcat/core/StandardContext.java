package com.mytomcat.core;

import com.mytomcat.Context;
import com.mytomcat.Request;
import com.mytomcat.Response;
import com.mytomcat.Wrapper;
import com.mytomcat.connector.http.HttpConnector;
import com.mytomcat.startup.Bootstrap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * servlet容器
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public class StandardContext extends ContainerBase implements Context {
    private HttpConnector connector = null;
    private ClassLoader loader = null;
    Map<String, String> servletClassMap = new ConcurrentHashMap<>();
    Map<String, StandardWrapper> servletInstanceMap = new ConcurrentHashMap<>();

    public StandardContext() {
        super();
        pipeline.setBasic(new StandardContextValve());
        name = "StandardContext";

        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(Bootstrap.WEB_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log("Container created.");
    }

    @Override
    public String getInfo() {
        return "My Servlet com.mytomcat.Context, version 0.1";
    }

    @Override
    public ClassLoader getLoader() {
        return this.loader;
    }

    @Override
    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public HttpConnector getConnector() {
        return connector;
    }

    public void setConnector(HttpConnector connector) {
        this.connector = connector;
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        super.invoke(request, response);
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public void setDisplayName(String displayName) {

    }

    @Override
    public String getDocBase() {
        return null;
    }

    @Override
    public void setDocBase(String docBase) {

    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public void setPath(String path) {

    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public void setSessionTimeout(int timeout) {

    }

    @Override
    public String getWrapperClass() {
        return null;
    }

    @Override
    public void setWrapperClass(String wrapperClass) {

    }

    @Override
    public Wrapper createWrapper() {
        return null;
    }

    @Override
    public String findServletMapping(String pattern) {
        return null;
    }

    @Override
    public String[] findServletMappings() {
        return new String[0];
    }

    @Override
    public void reload() {

    }

    public Wrapper getWrapper(String name) {
        StandardWrapper servletWrapper = servletInstanceMap.get(name);
        if (servletWrapper == null) {
            servletWrapper = new StandardWrapper(name, this);
            this.servletClassMap.put(name, name);
            this.servletInstanceMap.put(name, servletWrapper);
        }
        return servletWrapper;
    }
}
