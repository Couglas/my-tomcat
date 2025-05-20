package server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * servlet容器
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public class ServletContext extends ContainerBase {
    private HttpConnector connector = null;
    private ClassLoader loader = null;
    Map<String, String> servletClassMap = new ConcurrentHashMap<>();
    Map<String, ServletWrapper> servletInstanceMap = new ConcurrentHashMap<>();

    public ServletContext() {
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
    }

    @Override
    public String getInfo() {
        return "My Servlet Context, version 0.1";
    }

    @Override
    public ClassLoader getLoader() {
        return this.loader;
    }

    @Override
    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public HttpConnector getConnector() {
        return connector;
    }

    public void setConnector(HttpConnector connector) {
        this.connector = connector;
    }

    @Override
    public void invoke(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ServletWrapper servlet = null;
        String uri = ((HttpRequest) request).getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        servlet = servletInstanceMap.get(servletName);
        if (servlet == null) {
            servlet = new ServletWrapper(servletName, this);
            servletClassMap.put(servletName, servletName);
            servletInstanceMap.put(servletName, servlet);
        }

        try {
            HttpServletRequest requestFacade = new HttpRequestFacade(request);
            HttpServletResponse responseFacade = new HttpResponseFacade(response);
            System.out.println("call service");
            servlet.invoke(requestFacade, responseFacade);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
