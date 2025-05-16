package server;

import org.apache.commons.lang3.text.StrSubstitutor;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态资源处理器
 *
 * @author zhenxingchen4
 * @since 2025/5/15
 */
public class ServletProcessor {
    private static final String OK_MESSAGE = "HTTP/1.1 ${StatusCode} ${StatusName}\r\n"
            + "Content-Type: ${ContentType}\r\n"
            + "Server: mytomcat\r\n"
            + "Date: ${ZonedDateTime}\r\n"
            + "\r\n";

    public void process(HttpRequest request, Response response) {
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
        // 获取printWriter
        PrintWriter writer = null;
        try {
            response.setCharacterEncoding("UTF-8");
            writer = response.getWriter();
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
        // 写响应头
        String head = composeResponseHead();
        writer.println(head);
        // 创建servlet实例，调用service
        Servlet servlet = null;
        try {
            servlet = (Servlet) serverClass.newInstance();
            servlet.service(request, response);
        } catch (InstantiationException | IllegalAccessException | IOException | ServletException e) {
            e.printStackTrace();
        }
    }

    private String composeResponseHead() {
        Map<String, Object> map = new HashMap<>();
        map.put("StatusCode", "200");
        map.put("StatusName", "OK");
        map.put("ContentType", "text/html;charset=utf-8");
        map.put("ZonedDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now()));
        StrSubstitutor sub = new StrSubstitutor(map);
        return sub.replace(OK_MESSAGE);
    }
}
