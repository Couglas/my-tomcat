package test;


import javax.servlet.*;
import java.io.IOException;

/**
 * 测试Servlet
 *
 * @author zhenxingchen4
 * @since 2025/5/15
 */
public class HelloServlet implements Servlet {
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        servletResponse.setCharacterEncoding("UTF-8");
        String file = "<!DOCTYPE html> \n"
                + "<html>\n"
                + "<head><meta charset=\"utf-8\"><title>hello</title></head>\n"
                + "<body bgcolor=\"#f0f0f0\">\n"
                + "<h1 align=\"center\">" + "test servlet" + "</h1>\n";
        servletResponse.getWriter().println(file);
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
