package com.mytomcat.core;

import com.mytomcat.Request;
import com.mytomcat.Response;
import com.mytomcat.ValveContext;
import com.mytomcat.connector.HttpRequestFacade;
import com.mytomcat.connector.HttpResponseFacade;
import com.mytomcat.connector.http.HttpRequestImpl;
import com.mytomcat.connector.http.HttpResponseImpl;
import com.mytomcat.valves.ValveBase;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * wrapper valve实现类
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public class StandardWrapperValve extends ValveBase {
    @Override
    public void invoke(Request req, Response resp, ValveContext valveContext) throws IOException, ServletException {
        System.out.println("StandardWrapperValve invoke");
        HttpServletRequest requestFacade = new HttpRequestFacade((HttpRequestImpl) req);
        HttpServletResponse responseFacade = new HttpResponseFacade((HttpResponseImpl) resp);
        Servlet instance = ((StandardWrapper) getContainer()).getServlet();
        if (instance != null) {
            instance.service(requestFacade, responseFacade);
        }
    }
}
