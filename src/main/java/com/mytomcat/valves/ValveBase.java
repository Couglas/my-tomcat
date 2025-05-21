package com.mytomcat.valves;

import com.mytomcat.*;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 节点基类
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public abstract class ValveBase implements Valve {
    protected static String info = "Valve Base, version 0.1";
    protected Container container = null;
    protected int debug = 0;


    public int getDebug() {
        return debug;
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }
}
