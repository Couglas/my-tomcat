package com.mytomcat;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 自定义节点接口
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public interface Valve {
    String getInfo();

    Container getContainer();

    void setContainer(Container container);

    void invoke(Request req, Response resp, ValveContext valveContext) throws IOException, ServletException;
}
