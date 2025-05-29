package com.mytomcat.startup;

import com.mytomcat.Loader;
import com.mytomcat.connector.http.HttpConnector;
import com.mytomcat.core.CommonLoader;
import com.mytomcat.core.StandardHost;

import java.io.File;

/**
 * http服务
 *
 * @author zhenxingchen4
 * @since 2025/5/14
 */
public class Bootstrap {
    public static final String WEB_HOME = System.getProperty("user.dir");
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webapps";

    public static final int PORT = 8080;
    private static int debug = 0;

    public static void main(String[] args) {
        if (debug >= 1) {
            log("... my tomcat start up ...");
        }
        System.setProperty("mytomcat.base", WEB_ROOT);
        System.setProperty("mytomcat.home", WEB_HOME);
        HttpConnector connector = new HttpConnector();
        StandardHost container = new StandardHost();

        Loader loader = new CommonLoader();
        container.setLoader(loader);
        loader.start();

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
