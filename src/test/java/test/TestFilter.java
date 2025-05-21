package test;

import javax.servlet.*;
import java.io.IOException;

/**
 * filter测试类
 *
 * @author zhenxingchen4
 * @since 2025/5/21
 */
public class TestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("first filter test");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
