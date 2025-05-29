package com.mytomcat;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 容器
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public interface Container {
    String ADD_CHILD_EVENT = "addChild";
    String REMOVE_CHILD_EVENT = "removeChild";

    String getInfo();

    Loader getLoader();

    void setLoader(Loader loader);

    String getName();

    void setName(String name);

    Container getParent();

    void setParent(Container parent);

    void addChild(Container child);

    Container findChild(String name);

    Container[] findChildren();

    void invoke(Request req, Response resp) throws IOException, ServletException;

    void removeChild(Container child);

    Logger getLogger();

    void setLogger(Logger logger);
}
