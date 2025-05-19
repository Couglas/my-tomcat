package server;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 动态资源处理器
 *
 * @author zhenxingchen4
 * @since 2025/5/15
 */
public class ServletProcessor {

    public void process(HttpRequest request, HttpResponse response) {
        String uri = request.getUri();
        // 加载Servlet
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        Class<?> serverClass = null;
        try {
            serverClass = HttpConnector.loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        response.setCharacterEncoding("UTF-8");
        // 创建servlet实例，调用service
        Servlet servlet = null;
        try {
            servlet = (Servlet) serverClass.newInstance();
            System.out.println("call servlet");
            servlet.service(new HttpRequestFacade(request), new HttpResponseFacade(response));
        } catch (InstantiationException | IllegalAccessException | IOException | ServletException e) {
            e.printStackTrace();
        }
    }
}
