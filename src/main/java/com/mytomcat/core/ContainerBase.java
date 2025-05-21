package com.mytomcat.core;

import com.mytomcat.*;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 容器基类
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public abstract class ContainerBase implements Container, Pipeline {
    protected final Map<String, Container> children = new ConcurrentHashMap<>();
    protected ClassLoader loader = null;
    protected String name = null;
    protected Container parent = null;
    protected Logger logger = null;
    protected Pipeline pipeline = new StandardPipeline(this);

    public Pipeline getPipeline() {
        return pipeline;
    }

    @Override
    public synchronized void addValve(Valve valve) {
        pipeline.addValve(valve);
    }

    @Override
    public Valve getBasic() {
        return pipeline.getBasic();
    }

    @Override
    public void setBasic(Valve valve) {
        pipeline.setBasic(valve);
    }

    @Override
    public Valve[] getValves() {
        return pipeline.getValves();
    }

    @Override
    public void invoke(Request req, Response resp) throws IOException, ServletException {
        System.out.println("ContainerBase invoke");
        pipeline.invoke(req, resp);
    }

    @Override
    public void removeValve(Valve valve) {
        pipeline.removeValve(valve);
    }

    @Override
    public ClassLoader getLoader() {
        if (loader != null) {
            return loader;
        }
        if (parent != null) {
            return parent.getLoader();
        }
        return null;
    }

    @Override
    public synchronized void setLoader(ClassLoader loader) {
        if (loader == this.loader) {
            return;
        }
        this.loader = loader;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Container getParent() {
        return parent;
    }

    @Override
    public void setParent(Container parent) {
        this.parent = parent;
    }

    @Override
    public void addChild(Container child) {
        addChildInternal(child);
    }

    private void addChildInternal(Container child) {
        synchronized (children) {
            if (children.get(child.getName()) != null) {
                throw new IllegalArgumentException("add child: child name:" + child.getName() + "is not unique!");
            }
            child.setParent(this);
            children.put(child.getName(), child);
        }
    }

    @Override
    public Container findChild(String name) {
        if (name == null) {
            return null;
        }
        synchronized (children) {
            return children.get(name);
        }
    }

    @Override
    public Container[] findChildren() {
        synchronized (children) {
            return children.values().toArray(new Container[0]);
        }
    }

    @Override
    public void removeChild(Container child) {
        synchronized (children) {
            if (children.get(child.getName()) == null) {
                return;
            }
            children.remove(child.getName());
        }
        child.setParent(null);
    }

    @Override
    public Logger getLogger() {
        if (logger != null) {
            return logger;
        }
        if (parent != null) {
            return parent.getLogger();
        }
        return null;
    }

    @Override
    public synchronized void setLogger(Logger logger) {
        if (logger == this.logger) {
            return;
        }
        this.logger = logger;
    }

    protected void log(String msg) {
        Logger logger = getLogger();
        if (logger != null) {
            logger.log(logName() + ": " + msg);
        } else {
            System.out.println(logName() + ": " + msg);
        }
    }

    protected void log(String msg, Throwable throwable) {
        Logger logger = getLogger();
        if (logger != null) {
            logger.log(logName() + ": " + msg, throwable);
        } else {
            System.out.println(logName() + ": " + msg + ": " + throwable);
            throwable.printStackTrace(System.out);
        }
    }

    private String logName() {
        String clazz = this.getClass().getName();
        int period = clazz.indexOf(".");
        if (period >= 0) {
            clazz = clazz.substring(period + 1);
        }
        return clazz + "[" + getName() + "]";
    }

}
