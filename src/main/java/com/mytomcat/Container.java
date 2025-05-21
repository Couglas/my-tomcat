package com.mytomcat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    ClassLoader getLoader();

    void setLoader(ClassLoader loader);

    String getName();

    void setName();

    Container getParent();

    void setParent(Container parent);

    void addChild(Container child);

    Container findChild(String name);

    Container[] findChildren();

    void invoke(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException;

    void removeChild(Container child);
}
