package com.mytomcat.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 容器监听定义类
 *
 * @author zhenxingchen4
 * @since 2025/5/21
 */
public final class ContainerListenerDef {
    private String description;
    private String displayName;
    private String listenerClass;
    private String listenerName;
    private Map<String, String> parameters = new ConcurrentHashMap<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getListenerClass() {
        return listenerClass;
    }

    public void setListenerClass(String listenerClass) {
        this.listenerClass = listenerClass;
    }

    public String getListenerName() {
        return listenerName;
    }

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void addInitParameter(String name, String value) {
        this.parameters.put(name, value);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("ListenerDef[");
        sb.append("listenerName=");
        sb.append(this.listenerName);
        sb.append(", listenerClass=");
        sb.append(this.listenerClass);
        sb.append("]");
        return sb.toString();
    }
}
