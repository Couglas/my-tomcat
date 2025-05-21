package com.mytomcat;

/**
 * 连接器
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public interface Connector {
    Container getContainer();

    void setContainer(Container container);

    String getInfo();

    void setScheme(String scheme);

    Request createRequest();

    Response createResponse();

    void initialize();
}
