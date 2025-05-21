package com.mytomcat.connector.http;

import org.apache.commons.lang3.text.StrSubstitutor;
import com.mytomcat.startup.Bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 静态资源处理器
 *
 * @author zhenxingchen4
 * @since 2025/5/15
 */
public class StaticResourceProcessor {
    private static final int BUFFER_SIZE = 1024;
    private static final String FILE_NOT_FOUND_MESSAGE = "HTTP/1.1 404 File Not Found\r\n"
            + "Content-Type: text/html\r\n"
            + "Content-Length: 23\r\n"
            + "\r\n"
            + "<h1>File Not Found</h1>";
    private static final String OK_MESSAGE = "HTTP/1.1 ${StatusCode} ${StatusName}\r\n"
            + "Content-Type: ${ContentType}\r\n"
            + "Content-Length: ${ContentLength}\r\n"
            + "Server: mytomcat\r\n"
            + "Date: ${ZonedDateTime}\r\n"
            + "\r\n";


    public void process(HttpRequestImpl request, HttpResponseImpl response) throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        OutputStream output = null;
        try {
            output = response.getOutput();
            File file = new File(Bootstrap.WEB_ROOT, request.getUri());
            if (file.exists()) {
                String head = composeResponseHead(file);
                output.write(head.getBytes(StandardCharsets.UTF_8));
                fis = new FileInputStream(file);
                int ch = fis.read(bytes, 0, BUFFER_SIZE);
                while (ch != -1) {
                    output.write(bytes, 0, ch);
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                }
                output.flush();
            } else {
                output.write(FILE_NOT_FOUND_MESSAGE.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    private String composeResponseHead(File file) {
        Map<String, Object> map = new HashMap<>();
        map.put("StatusCode", "200");
        map.put("StatusName", "OK");
        map.put("ContentType", "text/html;charset=utf-8");
        map.put("ContentLength", file.length());
        map.put("ZonedDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now()));
        StrSubstitutor sub = new StrSubstitutor(map);
        return sub.replace(OK_MESSAGE);
    }
}
