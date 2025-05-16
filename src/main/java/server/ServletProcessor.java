package server;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * 动态资源处理器
 *
 * @author zhenxingchen4
 * @since 2025/5/15
 */
public class ServletProcessor {

    public void process(HttpRequest request, HttpResponse response) {
        String uri = request.getUri();
        // 构造类加载器
        URLClassLoader loader = null;
        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(HttpServer.WEB_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加载Servlet
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        Class<?> serverClass = null;
        try {
            serverClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        response.setCharacterEncoding("UTF-8");
        // 写响应头
        try {
            response.sendHeaders();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 创建servlet实例，调用service
        Servlet servlet = null;
        try {
            servlet = (Servlet) serverClass.newInstance();
            servlet.service(new HttpRequestFacade(request), new HttpResponseFacade(response));
        } catch (InstantiationException | IllegalAccessException | IOException | ServletException e) {
            e.printStackTrace();
        }
    }
}
