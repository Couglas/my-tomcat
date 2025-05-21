package com.mytomcat.core;

import com.mytomcat.Context;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.*;

/**
 * 应用过滤器配置
 *
 * @author zhenxingchen4
 * @since 2025/5/21
 */
public class ApplicationFilterConfig implements FilterConfig {
    private Context context = null;
    private Filter filter = null;
    private FilterDef filterDef = null;

    public ApplicationFilterConfig(Context context, FilterDef filterDef) throws ClassCastException, ClassNotFoundException,
            IllegalAccessException, InstantiationException, ServletException {
        super();
        this.context = context;
        setFilterDef(filterDef);
    }

    public void release() {
        if (this.filter != null) {
            filter.destroy();
        }
        this.filter = null;
    }

    public FilterDef getFilterDef() {
        return filterDef;
    }

    private void setFilterDef(FilterDef filterDef) throws ClassCastException, ClassNotFoundException,
            IllegalAccessException, InstantiationException, ServletException {
        this.filterDef = filterDef;
        if (filterDef == null) {
            if (this.filter != null) {
                this.filter.destroy();
            }
            this.filter = null;
        } else {
            this.filter = getFilter();
        }
    }

    public Filter getFilter() throws ClassCastException, ClassNotFoundException,
            IllegalAccessException, InstantiationException, ServletException {
        if (this.filter != null) {
            return this.filter;
        }

        String filterClass = filterDef.getFilterClass();
        ClassLoader classLoader = context.getLoader();
        Class<?> clazz = classLoader.loadClass(filterClass);
        this.filter = (Filter) clazz.newInstance();
        filter.init(this);

        return this.filter;
    }

    @Override
    public String getFilterName() {
        return filterDef.getFilterName();
    }

    @Override
    public ServletContext getServletContext() {
        return this.context.getServletContext();
    }

    @Override
    public String getInitParameter(String s) {
        Map<String, String> map = filterDef.getParameterMap();
        if (map == null) {
            return null;
        }
        return map.get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        Map<String, String> map = filterDef.getParameterMap();
        if (map == null) {
            return Collections.enumeration(new ArrayList<>());
        }
        return Collections.enumeration(map.keySet());
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("ApplicationFilterConfig[");
        sb.append("name=");
        sb.append(filterDef.getFilterName());
        sb.append(", filterClass=");
        sb.append(filterDef.getFilterClass());
        sb.append("]");
        return sb.toString();
    }
}
