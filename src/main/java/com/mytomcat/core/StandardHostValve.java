package com.mytomcat.core;

import com.mytomcat.Request;
import com.mytomcat.Response;
import com.mytomcat.ValveContext;
import com.mytomcat.connector.http.HttpRequestImpl;
import com.mytomcat.valves.ValveBase;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * host basic valve
 *
 * @author zhenxingchen4
 * @since 2025/5/22
 */
public class StandardHostValve extends ValveBase {
    @Override
    public void invoke(Request req, Response resp, ValveContext valveContext) throws IOException, ServletException {
        System.out.println("StandardHostValve invoke");
        String docbase = ((HttpRequestImpl) req).getDocbase();
        System.out.println("StandardHostValve invoke getDocbase: " + docbase);
        StandardHost host = (StandardHost) getContainer();
        StandardContext context = host.getContext(docbase);
        context.invoke(req, resp);
    }

}
