package com.mytomcat.core;

import com.mytomcat.Container;
import com.mytomcat.Loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;

/**
 * 通用加载器
 *
 * @author zhenxingchen4
 * @since 2025/5/29
 */
public class CommonLoader implements Loader {
    private ClassLoader classLoader;
    private ClassLoader parent;
    private String path;
    private String docBase;
    private Container container;

    public CommonLoader() {
    }

    public CommonLoader(ClassLoader parent) {
        this.parent = parent;
    }

    @Override
    public Container getContainer() {
        return container;
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
        return classLoader;
    }

    @Override
    public String getInfo() {
        return "CommonLoader..";
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
        System.out.println("Starting common loader, docBase: " + docBase);

        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(System.getProperty("mytomcat.home"));
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            repository = repository + "lib" + File.separator;
            urls[0] = new URL(null, repository, streamHandler);
            System.out.println("Common classLoader repository: " + repository);
            classLoader = new CommonClassLoader(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
