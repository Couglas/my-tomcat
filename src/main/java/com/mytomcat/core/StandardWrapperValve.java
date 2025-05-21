package com.mytomcat.core;

import com.mytomcat.Request;
import com.mytomcat.Response;
import com.mytomcat.ValveContext;
import com.mytomcat.connector.http.HttpRequestImpl;
import com.mytomcat.valves.ValveBase;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * wrapper valve实现类
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public class StandardWrapperValve extends ValveBase {
    private FilterDef filterDef = null;
    @Override
    public void invoke(Request req, Response resp, ValveContext valveContext) throws IOException, ServletException {
        System.out.println("StandardWrapperValve invoke");
        Servlet instance = ((StandardWrapper) getContainer()).getServlet();
        ApplicationFilterChain filterChain = createFilterChain(req, instance);
        if (instance != null && filterChain != null) {
            filterChain.doFilter((ServletRequest) req, (ServletResponse) resp);
        }
        filterChain.release();
    }

    private ApplicationFilterChain createFilterChain(Request request, Servlet servlet) {
        System.out.println("createFilterChain");
        if (servlet == null) {
            return null;
        }

        ApplicationFilterChain filterChain = new ApplicationFilterChain();
        filterChain.setServlet(servlet);
        StandardWrapper wrapper = (StandardWrapper) getContainer();
        StandardContext context = (StandardContext) wrapper.getParent();

        FilterMap[] filterMaps = context.findFilterMaps();
        if (filterMaps == null || filterMaps.length == 0) {
            return filterChain;
        }

        String requestPath = null;
        if (request instanceof HttpServletRequest) {
            String contextPath = "";
            String requestUri = ((HttpRequestImpl) request).getUri();
            if (requestUri.length() >= contextPath.length()) {
                requestPath = requestUri.substring(contextPath.length());
            }
        }
        for (int i = 0; i < filterMaps.length; i++) {
            if (!matchFiltersURL(filterMaps[i], requestPath)) {
                continue;
            }
            ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) context.findFilterConfig(filterMaps[i].getFilterName());
            if (filterConfig == null) {
                continue;
            }
            filterChain.addFilter(filterConfig);
        }
        String servletName = wrapper.getName();
        for (int i = 0; i < filterMaps.length; i++) {
            if (!matchFiltersServlet(filterMaps[i], servletName)) {
                continue;
            }
            ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) context.findFilterConfig(filterMaps[i].getFilterName());
            if (filterConfig == null) {
                continue;
            }
            filterChain.addFilter(filterConfig);
        }

        return filterChain;
    }

    private boolean matchFiltersURL(FilterMap filterMap, String requestPath) {
        if (requestPath == null) {
            return false;
        }
        String urlPattern = filterMap.getUrlPattern();
        if (urlPattern == null) {
            return false;
        }
        if (urlPattern.equals(requestPath)) {
            return true;
        }
        if (urlPattern.equals("/*")) {
            return true;
        }
        if (urlPattern.endsWith("/*")) {
            String comparePath = requestPath;
            while (true) {
                if (urlPattern.equals(comparePath + "/*")) {
                    return true;
                }
                int slash = comparePath.lastIndexOf("/");
                if (slash < 0) {
                    break;
                }
                comparePath = comparePath.substring(0, slash);
            }
            return false;
        }
        if (urlPattern.startsWith("*.")) {
            int slash = requestPath.lastIndexOf('/');
            int period = requestPath.lastIndexOf('.');
            if (slash >= 0 && period > slash) {
                return urlPattern.equals("*." + requestPath.substring(period + 1));
            }
        }
        return false;
    }

    private boolean matchFiltersServlet(FilterMap filterMap, String servletName) {
        if (servletName == null) {
            return false;
        }

        return servletName.equals(filterMap.getServletName());
    }

}
