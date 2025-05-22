package com.mytomcat.startup;

import com.mytomcat.Logger;
import com.mytomcat.connector.http.HttpConnector;
import com.mytomcat.core.ContainerListenerDef;
import com.mytomcat.core.FilterDef;
import com.mytomcat.core.FilterMap;
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

        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("TestFilter");
        filterDef.setFilterClass("test.TestFilter");
        container.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("TestFilter");
        filterMap.setUrlPattern("/*");
        container.addFilterMap(filterMap);
        container.filterStart();

        ContainerListenerDef listenerDef = new ContainerListenerDef();
        listenerDef.setListenerName("TestListener");
        listenerDef.setListenerClass("test.TestListener");
        container.addListenerDef(listenerDef);

        container.listenerStart();
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
