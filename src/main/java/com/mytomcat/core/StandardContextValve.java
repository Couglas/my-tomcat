package com.mytomcat.core;

import com.mytomcat.Request;
import com.mytomcat.Response;
import com.mytomcat.ValveContext;
import com.mytomcat.connector.http.HttpRequestImpl;
import com.mytomcat.valves.ValveBase;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * context valve实现类
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public class StandardContextValve extends ValveBase {
    private static final String info = "Standard Context Valve, version 0.1";

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void invoke(Request req, Response resp, ValveContext valveContext) throws IOException, ServletException {
        System.out.println("StandardContextValve invoke");
        StandardWrapper servletWrapper = null;
        String uri = ((HttpRequestImpl) req).getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        StandardContext context = (StandardContext) getContainer();
        servletWrapper = (StandardWrapper)context.getWrapper(servletName);

        System.out.println("Call service");
        servletWrapper.invoke(req, resp);
    }
}
