package server;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * http处理器
 *
 * @author zhenxingchen4
 * @since 2025/5/15
 */
public class HttpProcessor implements Runnable {
    private Socket socket;
    private boolean available = false;
    private HttpConnector connector;
    private int serverPort = 0;
    private boolean keepAlive = false;
    private boolean http11 = true;

    public HttpProcessor(HttpConnector connector) {
        this.connector = connector;
    }

    public void process(Socket socket) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
            keepAlive = true;
            while (keepAlive) {
                HttpRequest request = new HttpRequest(input);
                request.parse(socket);

                if (request.getSessionId() == null || request.getSessionId().isEmpty()) {
                    request.getSession(true);
                }

                HttpResponse response = new HttpResponse(output);
                response.setRequest(request);
                request.setResponse(response);
                response.sendHeaders();

                if (request.getUri().startsWith("/servlet/")) {
                    ServletProcessor processor = new ServletProcessor(this.connector);
                    processor.process(request, response);
                } else {
                    StaticResourceProcessor processor = new StaticResourceProcessor();
                    processor.process(request, response);
                }
                finishResponse(response);
                System.out.println("response header connection: " + response.getHeader("Connection"));
                if ("close".equals(response.getHeader("Connection"))) {
                    keepAlive = false;
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    private void finishResponse(HttpResponse response) {
        response.finishResponse();
    }

    @Override
    public void run() {
        while (true) {
            // 等可用的socket
            Socket socket = await();
            if (socket == null) {
                continue;
            }
            // 处理请求
            process(socket);

            // 回收当前processor
            connector.recycle(this);
        }
    }

    private synchronized Socket await() {
        while (!available) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        available = false;
        notifyAll();
        return this.socket;

    }

    public synchronized void assign(Socket socket) {
        while (available) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.socket = socket;
        available = true;
        notifyAll();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }
}
