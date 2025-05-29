package com.mytomcat.core;

import com.mytomcat.*;
import com.mytomcat.connector.http.HttpConnector;
import com.mytomcat.logger.FileLogger;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    private Map<String, String> servletClassMap = new ConcurrentHashMap<>();
    private Map<String, StandardWrapper> servletInstanceMap = new ConcurrentHashMap<>();
    private Map<String, ApplicationFilterConfig> filterConfigs = new ConcurrentHashMap<>();
    private Map<String, FilterDef> filterDefs = new ConcurrentHashMap<>();
    private FilterMap filterMaps[] = new FilterMap[0];
    private List<ContainerListenerDef> listenerDefs = new ArrayList<>();
    private List<ContainerListener> listeners = new ArrayList<>();

    public StandardContext() {
        super();
        pipeline.setBasic(new StandardContextValve());
        log("Container created.");
    }

    public void start() {
        Logger logger = new FileLogger();
        setLogger(logger);

        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("TestFilter");
        filterDef.setFilterClass("test.TestFilter");
        addFilterDef(filterDef);
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("TestFilter");
        filterMap.setUrlPattern("/*");
        addFilterMap(filterMap);
        filterStart();

        ContainerListenerDef listenerDef = new ContainerListenerDef();
        listenerDef.setListenerName("TestListener");
        listenerDef.setListenerClass("test.TestListener");
        addListenerDef(listenerDef);
        listenerStart();
        fireContainerEvent("Container started.", this);
    }

    private void fireContainerEvent(String type, Object data) {
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

    public void addListenerDef(ContainerListenerDef listenerDef) {
        synchronized (listenerDefs) {
            listenerDefs.add(listenerDef);
        }
    }

    public void addFilterDef(FilterDef filterDef) {
        filterDefs.put(filterDef.getFilterName(), filterDef);
    }

    public void addFilterMap(FilterMap filterMap) {
        String filterName = filterMap.getFilterName();
        String servletName = filterMap.getServletName();
        String urlPattern = filterMap.getUrlPattern();
        if (findFilterDef(filterName) == null) {
            throw new IllegalArgumentException("standardContext filterMap.name: " + filterName);
        }
        if (servletName == null && urlPattern == null) {
            throw new IllegalArgumentException("standardContext filterMap.either");
        }
        if (servletName != null && urlPattern != null) {
            throw new IllegalArgumentException("standardContext filterMap.either");
        }
        if (urlPattern != null && !validateURLPattern(urlPattern)) {
            throw new IllegalArgumentException("standardContext filterMap.pattern: " + urlPattern);
        }

        synchronized (filterMaps) {
            FilterMap[] results = new FilterMap[filterMaps.length + 1];
            System.arraycopy(filterMaps, 0, results, 0, filterMaps.length);
            results[filterMaps.length] = filterMap;
            filterMaps = results;
        }
    }

    private FilterDef findFilterDef(String filterName) {
        return filterDefs.get(filterName);
    }

    public FilterDef[] findFilterDefs() {
        synchronized (filterDefs) {
            FilterDef[] results = new FilterDef[filterDefs.size()];
            return filterDefs.values().toArray(results);
        }
    }

    public FilterMap[] findFilterMaps() {
        return filterMaps;
    }

    public void removeFilterDef(FilterDef filterDef) {
        filterDefs.remove(filterDef.getFilterName());
    }

    public void removeFilterMap(FilterMap filterMap) {
        synchronized (filterMaps) {
            int n = -1;
            for (int i = 0; i < filterMaps.length; i++) {
                if (filterMaps[i] == filterMap) {
                    n = i;
                    break;
                }
            }
            if (n < 0) {
                return;
            }

            FilterMap[] results = new FilterMap[filterMaps.length - 1];
            System.arraycopy(filterMaps, 0, results, 0, n);
            System.arraycopy(filterMaps, n + 1, results, n, filterMaps.length - 1 - n);
            filterMaps = results;
        }
    }

    public boolean filterStart() {
        System.out.println("Filter start...");
        boolean ok = true;
        synchronized (filterConfigs) {
            filterConfigs.clear();
            Iterator<String> names = filterDefs.keySet().iterator();
            while (names.hasNext()) {
                String name = names.next();
                ApplicationFilterConfig filterConfig = null;
                try {
                    filterConfig = new ApplicationFilterConfig(this, filterDefs.get(name));
                    filterConfigs.put(name, filterConfig);
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                         ServletException e) {
                    e.printStackTrace();
                    ok = false;
                }
            }
        }
        return ok;
    }

    public FilterConfig findFilterConfig(String name) {
        return filterConfigs.get(name);
    }

    private boolean validateURLPattern(String urlPattern) {
        if (urlPattern == null) {
            return false;
        }
        if (urlPattern.startsWith("*.")) {
            return urlPattern.indexOf('/') < 0;
        }

        return urlPattern.startsWith("/");
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


    @Override
    public String getInfo() {
        return "My Servlet com.mytomcat.Context, version 0.1";
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
        return this.docBase;
    }

    @Override
    public void setDocBase(String docBase) {
        this.docBase = docBase;
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
}
