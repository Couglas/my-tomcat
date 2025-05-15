package server;

import java.io.IOException;

/**
 * servlet
 *
 * @author zhenxingchen4
 * @since 2025/5/15
 */
public interface Servlet {
    void service(Request req, Response resp) throws IOException;
}
