package com.mytomcat.startup;

import com.mytomcat.Logger;
import com.mytomcat.connector.http.HttpConnector;
import com.mytomcat.core.StandardContext;
import com.mytomcat.logger.FileLogger;

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
        HttpConnector connector = new HttpConnector();
        StandardContext container = new StandardContext();
        connector.setContainer(container);
        container.setConnector(connector);
        Logger logger = new FileLogger();
        container.setLogger(logger);
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
