package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * http连接器
 *
 * @author zhenxingchen4
 * @since 2025/5/15
 */
public class HttpConnector implements Runnable {
    private int minProcessors = 3;
    private int maxProcessors = 10;
    private int curProcessors = 0;
    private Deque<HttpProcessor> processors = new ArrayDeque<>();

    public HttpConnector() {
        initProcessors();
    }

    @Override
    public void run() {
        int port = 8080;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                HttpProcessor processor = createProcessor();
                System.out.println("processors num: " + processors.size());
                if (processor == null) {
                    socket.close();
                    continue;
                }
                processor.assign(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initProcessors() {
        for (int i = 0; i < minProcessors; i++) {
            HttpProcessor processor = new HttpProcessor(this);
            processor.start();
            processors.push(processor);

        }
        curProcessors = minProcessors;
    }

    private HttpProcessor createProcessor() {
        synchronized (processors) {
            if (processors.size() > 0) {
                return processors.pop();
            }
            if (curProcessors < maxProcessors) {
                return newProcessor();
            }
            return null;
        }
    }

    private HttpProcessor newProcessor() {
        HttpProcessor processor = new HttpProcessor(this);
        processor.start();
        processors.push(processor);
        curProcessors++;
        return processors.pop();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void recycle(HttpProcessor processor) {
        processors.push(processor);
    }
}
