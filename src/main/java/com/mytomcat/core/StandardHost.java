package com.mytomcat.core;

import com.mytomcat.*;
import com.mytomcat.connector.http.HttpConnector;
import com.mytomcat.loader.WebappLoader;
import com.mytomcat.logger.FileLogger;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * host容器
 *
 * @author zhenxingchen4
 * @since 2025/5/22
 */
public class StandardHost extends ContainerBase {
    private static final String info = "Standard Host, version 0.1";
    private HttpConnector connector;
    private Map<String, StandardContext> contextMap = new ConcurrentHashMap<>();
    private List<ContainerListenerDef> listenerDefs = new ArrayList<>();
    private List<ContainerListener> listeners = new ArrayList<>();

    public StandardHost() {
        super();
        pipeline.setBasic(new StandardHostValve());
        log("Host created.");
    }

    @Override
    public String getInfo() {
        return info;
    }

    public HttpConnector getConnector() {
        return connector;
    }

    public void setConnector(HttpConnector connector) {
        this.connector = connector;
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        System.out.println("StandardHost invoke");
        super.invoke(request, response);
    }

    public StandardContext getContext(String name) {
        StandardContext context = contextMap.get(name);
        if (context == null) {
            System.out.println("loading context: " + name);
            context = new StandardContext();
            context.setDocBase(name);
            context.setConnector(connector);

            Loader loader = new WebappLoader(name, this.loader.getClassLoader());
            context.setLoader(loader);
            loader.start();
            context.start();

            this.contextMap.put(name, context);
        }

        return context;
    }

    public void start() {
        fireContainerEvent("Host started", this);
        Logger logger = new FileLogger();
        setLogger(logger);
//        ContainerListenerDef listenerDef = new ContainerListenerDef();
//        listenerDef.setListenerName("TestListener");
//        listenerDef.setListenerClass("test.TestListener");
//        addListenerDef(listenerDef);
        listenerStart();

        File classPath = new File(System.getProperty("mytomcat.base"));
        String[] dirs = classPath.list();
        for (int i = 0; i < dirs.length; i++) {
            getContext(dirs[i]);
        }

    }

    public boolean listenerStart() {
        System.out.println("Listener start...");
        boolean ok = true;
        synchronized (listeners) {
            listeners.clear();
            Iterator<ContainerListenerDef> defs = listenerDefs.iterator();
            while (defs.hasNext()) {
                ContainerListenerDef def = defs.next();
                ContainerListener listener = null;
                String listenerClass = def.getListenerClass();
                Loader classLoader = this.getLoader();
                try {
                    Class<?> clazz = classLoader.getClassLoader().loadClass(listenerClass);
                    listener = (ContainerListener) clazz.newInstance();
                    addContainerListener(listener);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    ok = false;
                }
            }
        }
        return ok;
    }

    public void addListenerDef(ContainerListenerDef listenerDef) {
        synchronized (listenerDefs) {
            listenerDefs.add(listenerDef);
        }
    }

    public void fireContainerEvent(String type, Object data) {
        if (listeners.isEmpty()) {
            return;
        }

        ContainerEvent event = new ContainerEvent(this, type, data);
        ContainerListener[] list = new ContainerListener[0];
        synchronized (listeners) {
            list = listeners.toArray(list);
        }

        for (ContainerListener listener : list) {
            listener.containerEvent(event);
        }
    }

    public void addContainerListener(ContainerListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeContainerListener(ContainerListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }


}
