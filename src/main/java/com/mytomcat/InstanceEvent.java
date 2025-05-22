package com.mytomcat;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.EventObject;

/**
 * 实例事件
 *
 * @author zhenxingchen4
 * @since 2025/5/21
 */
public final class InstanceEvent extends EventObject {
    public static final String BEFORE_INIT_EVENT = "beforeInit";
    public static final String AFTER_INIT_EVENT = "afterInit";
    public static final String BEFORE_SERVICE_EVENT = "beforeService";
    public static final String AFTER_SERVICE_EVENT = "afterService";
    public static final String BEFORE_DESTROY_EVENT = "beforeDestroy";
    public static final String AFTER_DESTROY_EVENT = "afterDestroy";
    public static final String BEFORE_DISPATCH_EVENT = "beforeDispatch";
    public static final String AFTER_DISPATCH_EVENT = "afterDispatch";
    public static final String BEFORE_FILTER_EVENT = "beforeFilter";
    public static final String AFTER_FILTER_EVENT = "afterFilter";

    private Throwable exception;
    private Filter filter;
    private ServletRequest request;
    private ServletResponse response;
    private Servlet servlet;
    private String type;
    private Wrapper wrapper;

    public InstanceEvent(Wrapper wrapper, Filter filter, String type) {
        super(wrapper);
        this.wrapper = wrapper;
        this.filter = filter;
        this.type = type;
    }

    public InstanceEvent(Wrapper wrapper, Filter filter, String type, Throwable exception) {
        super(wrapper);
        this.wrapper = wrapper;
        this.filter = filter;
        this.type = type;
        this.exception = exception;
    }

    public InstanceEvent(Wrapper wrapper, Filter filter, String type, ServletRequest request, ServletResponse response) {
        super(wrapper);
        this.wrapper = wrapper;
        this.filter = filter;
        this.type = type;
        this.request = request;
        this.response = response;
    }

    public InstanceEvent(Wrapper wrapper, Filter filter, String type, ServletRequest request, ServletResponse response,
                         Throwable exception) {
        super(wrapper);
        this.wrapper = wrapper;
        this.filter = filter;
        this.type = type;
        this.request = request;
        this.response = response;
        this.exception = exception;
    }


    public InstanceEvent(Wrapper wrapper, Servlet servlet, String type) {
        super(wrapper);
        this.wrapper = wrapper;
        this.servlet = servlet;
        this.type = type;
    }

    public InstanceEvent(Wrapper wrapper, Servlet servlet, String type, Throwable exception) {
        super(wrapper);
        this.wrapper = wrapper;
        this.servlet = servlet;
        this.type = type;
        this.exception = exception;
    }

    public InstanceEvent(Wrapper wrapper, Servlet servlet, String type, ServletRequest request, ServletResponse response) {
        super(wrapper);
        this.wrapper = wrapper;
        this.servlet = servlet;
        this.type = type;
        this.request = request;
        this.response = response;
    }

    public InstanceEvent(Wrapper wrapper, Servlet servlet, String type, ServletRequest request, ServletResponse response,
                         Throwable exception) {
        super(wrapper);
        this.wrapper = wrapper;
        this.servlet = servlet;
        this.type = type;
        this.request = request;
        this.response = response;
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }

    public Filter getFilter() {
        return filter;
    }

    public ServletRequest getRequest() {
        return request;
    }

    public ServletResponse getResponse() {
        return response;
    }

    public Servlet getServlet() {
        return servlet;
    }

    public String getType() {
        return type;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }
}
