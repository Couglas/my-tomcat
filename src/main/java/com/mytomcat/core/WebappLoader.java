package com.mytomcat.core;

import com.mytomcat.Container;
import com.mytomcat.Loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;

/**
 * web应用加载器
 *
 * @author zhenxingchen4
 * @since 2025/5/29
 */
public class WebappLoader implements Loader {
    private ClassLoader classLoader;
    private ClassLoader parent;
    private String path;
    private String docBase;
    private Container container;

    public WebappLoader(String docBase) {
        this.docBase = docBase;
    }

    public WebappLoader(String docBase, ClassLoader parent) {
        this.parent = parent;
        this.docBase = docBase;
    }

    @Override
    public Container getContainer() {
        return this.container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getDocBase() {
        return docBase;
    }

    @Override
    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public String getInfo() {
        return "WebappLoader..";
    }

    @Override
    public void addRepository(String repository) {

    }

    @Override
    public String[] findRepositories() {
        return new String[0];
    }

    @Override
    public synchronized void start() {
        System.out.println("Starting webapp loader, docBase: " + docBase);
        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(System.getProperty("mytomcat.base"));
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            if (docBase != null && !docBase.isEmpty()) {
                repository = repository + docBase + File.separator;
            }
            repository = repository + "WEB-INF" + File.separator + "classes" + File.separator;
            urls[0] = new URL(null, repository, streamHandler);
            System.out.println("Webapp ClassLoader repository: " + repository);
            classLoader = new WebappClassLoader(urls, parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
