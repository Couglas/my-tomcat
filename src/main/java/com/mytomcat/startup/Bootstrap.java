package com.mytomcat.startup;

import com.mytomcat.Loader;
import com.mytomcat.connector.http.HttpConnector;
import com.mytomcat.loader.CommonLoader;
import com.mytomcat.core.StandardHost;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;

/**
 * http服务
 *
 * @author zhenxingchen4
 * @since 2025/5/14
 */
public class Bootstrap {
    public static final String WEB_HOME = System.getProperty("user.dir");
    public static String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webapps";
    public static int PORT = 8080;
    private static int debug = 0;

    public static void main(String[] args) {
        if (debug >= 1) {
            log("... my tomcat start up ...");
        }

        String file = WEB_HOME + File.separator + "conf" + File.separator + "server.xml";
        SAXReader reader = new SAXReader();
        Document document;
        
        try {
            document = reader.read(file);
            Element root = document.getRootElement();
            Element connectorElement = root.element("Connector");
            Attribute portAttribute = connectorElement.attribute("port");
            PORT = Integer.parseInt(portAttribute.getText());
            Element hostElement = root.element("Host");
            Attribute appBaseAttribute = hostElement.attribute("appBase");
            WEB_ROOT = WEB_ROOT + File.separator + appBaseAttribute.getText();
        } catch (DocumentException e) {
            System.out.println(e.getMessage());
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
