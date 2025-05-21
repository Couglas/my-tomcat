package com.mytomcat.core;

import com.mytomcat.connector.HttpRequestFacade;
import com.mytomcat.connector.HttpResponseFacade;
import com.mytomcat.connector.http.HttpRequestImpl;
import com.mytomcat.connector.http.HttpResponseImpl;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 应用过滤器链
 *
 * @author zhenxingchen4
 * @since 2025/5/21
 */
public class ApplicationFilterChain implements FilterChain {
    private List<ApplicationFilterConfig> filters = new ArrayList<>();
    private Iterator<ApplicationFilterConfig> iterator = null;
    private Servlet servlet;

    public ApplicationFilterChain() {
        super();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        System.out.println("FilterChain doFilter");
        internalDoFilter(servletRequest, servletResponse);
    }

    private void internalDoFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
        if (iterator == null) {
            iterator = filters.iterator();
        }
        if (iterator.hasNext()) {
            ApplicationFilterConfig filterConfig = iterator.next();
            Filter filter = null;

            try {
                filter = filterConfig.getFilter();
                System.out.println("Filter doFilter");
                filter.doFilter(req, resp, this);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new ServletException(e);
            }
            return;
        }

        HttpRequestFacade requestFacade = new HttpRequestFacade((HttpRequestImpl) req);
        HttpResponseFacade responseFacade = new HttpResponseFacade((HttpResponseImpl) resp);
        try {
            servlet.service(requestFacade, responseFacade);
        } catch (ServletException | IOException  e) {
            throw new ServletException(e);
        }

    }

    public void addFilter(ApplicationFilterConfig filterConfig) {
        this.filters.add(filterConfig);
    }

    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    public void release() {
        this.filters.clear();
        this.iterator = null;
        this.servlet = null;
    }
}
