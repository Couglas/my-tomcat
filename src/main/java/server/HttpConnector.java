package server;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

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
    public static Map<String, HttpSession> sessions = new ConcurrentHashMap<>();
    public static URLClassLoader loader = null;

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

        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(HttpServer.WEB_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                HttpProcessor processor = createProcessor();
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

    public static Session createSession() {
        Session session = new Session();
        session.setValid(true);
        session.setCreationTime(System.currentTimeMillis());
        String sessionId = generateSessionId();
        session.setId(sessionId);
        sessions.put(sessionId, session);
        return session;
    }

    private static synchronized String generateSessionId() {
        Random random = new Random();
        long seed = System.currentTimeMillis();
        random.setSeed(seed);
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            byte b1 = (byte) ((bytes[i] & 0xf0) >> 4);
            byte b2 = (byte) (bytes[i] & 0x0f);
            if (b1 < 10) {
                result.append((char) ('0' + b1));
            } else {
                result.append((char) ('A' + (b1 - 10)));
            }
            if (b2 < 10) {
                result.append((char) ('0' + b2));
            } else {
                result.append((char) ('A' + (b2 - 10)));
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(generateSessionId());
    }
}
