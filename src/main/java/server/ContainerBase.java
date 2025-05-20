package server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 容器基类
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public abstract class ContainerBase implements Container {
    protected final Map<String, Container> children = new ConcurrentHashMap<>();
    protected ClassLoader loader = null;
    protected String name = null;
    protected Container parent = null;

    @Override
    public abstract String getInfo();

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
    public void setName() {
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
    public void invoke(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

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
}
