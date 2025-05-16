package server;

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

    public HttpProcessor(HttpConnector connector) {
        this.connector = connector;
    }

    public void process(Socket socket) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        InputStream input = null;
        OutputStream output = null;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();

            HttpRequest request = new HttpRequest(input);
            request.parse(socket);
            HttpResponse response = new HttpResponse(output);
            response.setRequest(request);

            if (request.getUri().startsWith("/servlet/")) {
                ServletProcessor processor = new ServletProcessor();
                processor.process(request, response);
            } else {
                StaticResourceProcessor processor = new StaticResourceProcessor();
                processor.process(request, response);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
