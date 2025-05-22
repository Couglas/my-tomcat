package com.mytomcat.startup;

import com.mytomcat.connector.http.HttpConnector;
import com.mytomcat.core.StandardHost;
import com.mytomcat.core.WebappClassLoader;

import java.io.File;

/**
 * http服务
 *
 * @author zhenxingchen4
 * @since 2025/5/14
 */
public class Bootstrap {
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    private static int debug = 0;

    public static void main(String[] args) {
        if (debug >= 1) {
            log("... my tomcat start up ...");
        }
        System.setProperty("mytomcat.base", WEB_ROOT);
        HttpConnector connector = new HttpConnector();
        StandardHost container = new StandardHost();
        WebappClassLoader loader = new WebappClassLoader();
        loader.start();
        container.setLoader(loader);
        connector.setContainer(container);
        container.setConnector(connector);
        container.start();
        connector.start();

    }

    private static void log(String msg) {
        System.out.print("Bootstrap: ");
        System.out.println(msg);
    }

    private static void log(String message, Throwable exception) {
        log(message);
        exception.printStackTrace(System.out);
    }

}
