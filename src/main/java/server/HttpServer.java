package server;

import java.io.File;

/**
 * http服务
 *
 * @author zhenxingchen4
 * @since 2025/5/14
 */
public class HttpServer {
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.await();
    }

    public void await() {
        HttpConnector connector = new HttpConnector();
        ServletContext container = new ServletContext();
        connector.setContainer(container);
        container.setConnector(connector);
        connector.start();
    }
}
