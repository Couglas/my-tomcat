package test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * session测试servlet
 *
 * @author zhenxingchen4
 * @since 2025/5/19
 */
public class SessionTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("test do get");
        System.out.println("param name : " + req.getParameter("name"));
        HttpSession session = req.getSession(true);
        String user = (String) session.getAttribute("user");
        System.out.println("user from session: " + user);
        if (user == null || user.isEmpty()) {
            session.setAttribute("user", "couglas");
        }

        resp.setCharacterEncoding("UTF-8");
        String file = "<!DOCTYPE html> \n"
                + "<html> \n"
                + "<head><meta charset=\"utf-8\"><title>test get</title></head> \n"
                + "<body bgcolor=\"#f0f0f0\">\n"
                + "<h1 align=\"center\"> session test </h1> \n";
        System.out.println(file);
        resp.getWriter().println(file);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("test do post");
        System.out.println("param name: " + req.getParameter("name"));
        resp.setCharacterEncoding("UTF-8");
        String file = "<!DOCTYPE html> \n"
                + "<html> \n"
                + "<head><meta charset=\"utf-8\"><title>test post</title></head> \n"
                + "<body bgcolor=\"#f0f0f0\">\n"
                + "<h1 align=\"center\"> session test </h1> \n";
        System.out.println(file);
        resp.getWriter().println(file);
    }
}
