package com.mytomcat.core;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 通用类加载器
 *
 * @author zhenxingchen4
 * @since 2025/5/29
 */
public class CommonClassLoader extends URLClassLoader {
    protected boolean delegate = false;
    private ClassLoader parent;
    private ClassLoader system;

    public CommonClassLoader() {
        super(new URL[0]);
        this.parent = getParent();
        system = getSystemClassLoader();
    }

    public CommonClassLoader(URL[] urls) {
        super(urls);
        this.parent = getParent();
        system = getSystemClassLoader();
    }

    public CommonClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
        this.parent = parent;
        system = getSystemClassLoader();
    }

    public CommonClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.parent = parent;
        this.system = getSystemClassLoader();
    }

    public boolean isDelegate() {
        return delegate;
    }

    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> clazz;
        try {
            clazz = super.findClass(name);
        } catch (RuntimeException e) {
            throw e;
        }
        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }

        return clazz;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz;

        try {
            clazz = system.loadClass(name);
            if (clazz != null) {
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        boolean delegateLoad = delegate;
        if (delegateLoad) {
            ClassLoader loader = parent;
            if (loader == null) {
                loader = system;
            }
            try {
                clazz = loader.loadClass(name);
                if (clazz != null) {
                    if (resolve) {
                        resolveClass(clazz);
                    }
                    return clazz;
                }
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }

        try {
            clazz = findClass(name);
            if (clazz != null) {
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        if (!delegateLoad) {
            ClassLoader loader = parent;
            if (loader == null) {
                loader = system;
            }
            try {
                clazz = loader.loadClass(name);
                if (clazz != null) {
                    if (resolve) {
                        resolveClass(clazz);
                    }
                    return clazz;
                }
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }

        throw new ClassNotFoundException(name);
    }

    private void log(String message) {
        System.out.println("CommonClassLoader: " + message);
    }

    private void log(String message, Throwable throwable) {
        System.out.println("CommonClassLoader: " + message);
        throwable.printStackTrace(System.out);
    }
}
