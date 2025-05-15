package test;


import server.Request;
import server.Response;
import server.Servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 测试Servlet
 *
 * @author zhenxingchen4
 * @since 2025/5/15
 */
public class HelloServlet implements Servlet {
    @Override
    public void service(Request req, Response resp) throws IOException {
        String file = "<!DOCTYPE html> \n"
                + "<html>\n"
                + "<head><meta charset=\"utf-8\"><title>hello</title></head>\n"
                + "<body bgcolor=\"#f0f0f0\">\n"
                + "<h1 align=\"center\">" + "test servlet" + "</h1>\n";
        resp.getOutput().write(file.getBytes(StandardCharsets.UTF_8));
    }
}
