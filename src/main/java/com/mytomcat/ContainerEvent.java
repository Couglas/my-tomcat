package com.mytomcat;

import java.util.EventObject;

/**
 * 容器事件
 *
 * @author zhenxingchen4
 * @since 2025/5/21
 */
public final class ContainerEvent extends EventObject {
    private Container container = null;
    private String type = null;
    private Object data = null;

    public ContainerEvent(Container container, String type, Object data) {
        super(container);
        this.container = container;
        this.type = type;
        this.data = data;
    }

    public Container getContainer() {
        return container;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    @Override
    public Object getSource() {
        return super.getSource();
    }

    @Override
    public String toString() {
        return "ContainerEvent['" + getContainer() + "', '" + getType() + "', '" + getData() + "']";
    }
}
