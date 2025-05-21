package com.mytomcat;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 责任链
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public interface Pipeline {
    Valve getBasic();

    void setBasic(Valve valve);

    void addValve(Valve valve);

    Valve[] getValves();

    void invoke(Request req, Response resp) throws IOException, ServletException;

    void removeValve(Valve valve);
}
