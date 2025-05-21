package com.mytomcat;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 调用自定义节点
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public interface ValveContext {
    String getInfo();

    void invokeNext(Request req, Response resp) throws IOException, ServletException;

}
