package com.mytomcat.logger;

import com.mytomcat.util.StringManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

/**
 * 文件日志
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public class FileLogger extends LoggerBase {
    protected static final String info = "File Logger, version 0.1";
    private String date = "";
    private String directory = "logs";
    private String prefix = "mytomcat.";
    private String suffix = ".log";
    private StringManager manager = StringManager.getManager(Constants.Package);
    private boolean started = false;
    private boolean timestamp = true;
    private PrintWriter writer = null;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean isTimestamp() {
        return timestamp;
    }

    public void setTimestamp(boolean timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void log(String msg) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String tsStr = ts.toString().substring(0, 19);
        String tsDate = tsStr.substring(0, 10);
        if (!date.equals(tsDate)) {
            synchronized (this) {
                if (!date.equals(tsDate)) {
                    close();
                    date = tsDate;
                    open();
                }
            }
        }
        if (writer != null) {
            if (timestamp) {
                writer.println(tsStr + " " + msg);
            } else {
                writer.println(msg);
            }
        }
    }

    private void open() {
        File dir = new File(directory);
        if (!dir.isAbsolute()) {
            dir = new File(System.getProperty("catalina.base"), directory);
        }
        dir.mkdirs();

        try {
            String pathName = dir.getAbsolutePath() + File.separator + prefix + date + suffix;
            writer = new PrintWriter(new FileWriter(pathName, true), true);
        } catch (IOException e) {
            writer = null;
            e.printStackTrace();
        }
    }

    private void close() {
        if (writer == null) {
            return;
        }
        writer.flush();
        writer.close();
        writer = null;
        date = "";
    }


}
