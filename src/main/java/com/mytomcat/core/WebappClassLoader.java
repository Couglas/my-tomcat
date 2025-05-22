package com.mytomcat.core;

import com.mytomcat.Container;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * web应用类加载器
 *
 * @author zhenxingchen4
 * @since 2025/5/22
 */
public class WebappClassLoader extends ClassLoader {
    private ClassLoader classLoader;
    private String path;
    private String docbase;
    private Container container;

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDocbase() {
        return docbase;
    }

    public void setDocbase(String docbase) {
        this.docbase = docbase;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public String getInfo() {
        return "web app loader";
    }

    public void addRepository(String repository) {

    }

    public String[] findRepositories() {
        return null;
    }

    public synchronized void start() {
        System.out.println("Start WebappLoader");
        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(System.getProperty("mytomcat.base"));
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            if (docbase != null && !docbase.isEmpty()) {
                repository = repository + docbase + File.separator;
            }

            urls[0] = new URL(null, repository, streamHandler);
            System.out.println("Web app class loader repository: " + repository);
            classLoader = new URLClassLoader(urls);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {

    }
}
