package server;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 动态资源处理器
 *
 * @author zhenxingchen4
 * @since 2025/5/15
 */
public class ServletProcessor {
    private HttpConnector connector;

    public ServletProcessor(HttpConnector connector) {
        this.connector = connector;
    }

    public void process(HttpRequest request, HttpResponse response) throws IOException, ServletException {
        this.connector.getContainer().invoke(request, response);
    }
}
