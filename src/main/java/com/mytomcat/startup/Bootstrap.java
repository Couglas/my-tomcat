package com.mytomcat.startup;

import com.mytomcat.core.StandardContext;
import com.mytomcat.connector.http.HttpConnector;

import java.io.File;

/**
 * http服务
 *
 * @author zhenxingchen4
 * @since 2025/5/14
 */
public class Bootstrap {
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    public static void main(String[] args) {
        Bootstrap server = new Bootstrap();
        server.await();
    }

    public void await() {
        HttpConnector connector = new HttpConnector();
        StandardContext container = new StandardContext();
        connector.setContainer(container);
        container.setConnector(connector);
        connector.start();
    }
}
