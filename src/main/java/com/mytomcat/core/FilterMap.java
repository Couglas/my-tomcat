package com.mytomcat.core;

import com.mytomcat.util.URLDecoder;

/**
 * filter map
 *
 * @author zhenxingchen4
 * @since 2025/5/21
 */
public class FilterMap {
    private String filterName = null;
    private String servletName = null;
    private String urlPattern = null;


    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = URLDecoder.URLDecode(urlPattern);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("FilterMap[");
        sb.append("filterName=");
        sb.append(filterName);
        if (servletName != null) {
            sb.append(", servletName=");
            sb.append(servletName);
        }
        if (urlPattern != null) {
            sb.append(", urlPattern=");
            sb.append(urlPattern);
        }
        sb.append("]");
        return sb.toString();
    }
}
