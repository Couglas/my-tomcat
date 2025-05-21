package com.mytomcat.core;

import com.mytomcat.*;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * 责任链实现类
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public class StandardPipeline implements Pipeline {
    private static final String info = "Standard Pipeline, version 0.1";
    protected Valve basic = null;
    protected Container container = null;
    protected int debug = 0;
    protected Valve[] valves = new Valve[0];


    public StandardPipeline() {
        this(null);
    }

    public StandardPipeline(Container container) {
        super();
        setContainer(container);
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Valve getBasic() {
        return this.basic;
    }

    @Override
    public void setBasic(Valve valve) {
        if (this.basic == valve) {
            return;
        }
        if (valve == null) {
            return;
        }

        valve.setContainer(container);
        this.basic = valve;
    }

    @Override
    public void addValve(Valve valve) {
        synchronized (valves) {
            Valve[] results = new Valve[valves.length + 1];
            System.arraycopy(valves, 0, results, 0, valves.length);
            valve.setContainer(container);
            results[valves.length] = valve;
            valves = results;
        }
    }

    @Override
    public Valve[] getValves() {
        if (basic == null) {
            return valves;
        }

        synchronized (valves) {
            Valve[] results = new Valve[valves.length + 1];
            System.arraycopy(valves, 0, results, 0, valves.length);
            results[valves.length] = basic;
            return results;
        }
    }

    @Override
    public void invoke(Request req, Response resp) throws IOException, ServletException {
        System.out.println("StandardPipeline invoke");
        StandardPipelineValveContext valveContext = new StandardPipelineValveContext();
        valveContext.invokeNext(req, resp);
    }

    @Override
    public void removeValve(Valve valve) {
        synchronized (valves) {
            int j = -1;
            for (int i = 0; i < valves.length; i++) {
                if (valve == valves[i]) {
                    j = i;
                    break;
                }
            }
            if (j < 0) {
                return;
            }
            valve.setContainer(null);
            Valve[] results = new Valve[valves.length - 1];
            int n = 0;
            for (int i = 0; i < valves.length; i++) {
                if (i == j) {
                    continue;
                }
                results[n++] = valves[i];
            }
            valves = results;
        }
    }

    protected class StandardPipelineValveContext implements ValveContext {
        protected int stage = 0;

        @Override
        public String getInfo() {
            return info;
        }

        @Override
        public void invokeNext(Request req, Response resp) throws IOException, ServletException {
            System.out.println("StandardPipelineValveContext invokeNext");
            int subscript = stage;
            stage = stage + 1;

            if (subscript < valves.length) {
                valves[subscript].invoke(req, resp, this);
            } else if ((subscript == valves.length) && basic != null) {
                basic.invoke(req, resp, this);
            } else {
                throw new ServletException("standard pipeline no valve");
            }
        }
    }

    protected void log(String message) {
        Logger logger = null;
        if (container != null)
            logger = container.getLogger();
        if (logger != null) {
            logger.log("StandardPipeline[" + container.getName() + "]: " + message);
        } else {
            System.out.println("StandardPipeline[" + container.getName() + "]: " + message);
        }

    }

    protected void log(String message, Throwable throwable) {
        Logger logger = null;
        if (container != null)
            logger = container.getLogger();
        if (logger != null) {
            logger.log("StandardPipeline[" + container.getName() + "]: " + message, throwable);
        } else {
            System.out.println("StandardPipeline[" + container.getName() + "]: " + message);
            throwable.printStackTrace(System.out);
        }

    }
}
