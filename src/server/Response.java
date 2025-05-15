package server;

import java.io.*;

/**
 * 响应
 *
 * @author zhenxingchen4
 * @since 2025/5/14
 */
public class Response {
    private static final int BUFFER_SIZE = 1024;
    private Request request;
    private OutputStream output;

    public Response(OutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        String uri = request.getUri();
        System.out.println("uri: " + uri);

        File file = new File(HttpServer.WEB_ROOT, uri);
        try {
            if (file.exists()) {
                String header = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: " + file.length() + "\r\n" +
                        "\r\n";
                output.write(header.getBytes());
                fis = new FileInputStream(file);
                int ch = fis.read(bytes, 0, BUFFER_SIZE);
                while (ch != -1) {
                    output.write(bytes, 0, ch);
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                }
                output.flush();
            } else {
                String errorMessage = "HTTP/1.1 404 File Not Found\r\n"
                        + "Content-Type: text/html\r\n"
                        + "Content-Length: 23\r\n"
                        + "\r\n"
                        + "<h1>File Not Found</h1>";
                output.write(errorMessage.getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
}
