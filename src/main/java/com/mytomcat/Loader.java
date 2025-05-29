package com.mytomcat;

/**
 * 加载器
 *
 * @author zhenxingchen4
 * @since 2025/5/29
 */
public interface Loader {
    Container getContainer();

    void setContainer(Container container);

    String getPath();

    void setPath(String path);

    String getDocBase();

    void setDocBase(String docBase);

    ClassLoader getClassLoader();

    String getInfo();

    void addRepository(String repository);

    String[] findRepositories();

    void start();

    void stop();
}
