package com.mytomcat.logger;

import com.mytomcat.Logger;

import javax.servlet.ServletException;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * 日志基类
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public abstract class LoggerBase implements Logger {
    protected int debug = 0;
    protected static final String info = "My Logger Base, version 1.0";
    protected int verbosity = ERROR;

    public int getDebug() {
        return debug;
    }

    public void setDebug(int debug) {
        this.debug = debug;
    }

    @Override
    public int getVerbosity() {
        return verbosity;
    }

    @Override
    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    @Override
    public String getInfo() {
        return info;
    }

    public void setVerbosityLevel(String verbosity) {
        if ("FATAL".equalsIgnoreCase(verbosity)) {
            this.verbosity = FATAL;
        } else if ("ERROR".equalsIgnoreCase(verbosity)) {
            this.verbosity = ERROR;
        } else if ("WARNING".equalsIgnoreCase(verbosity)) {
            this.verbosity = WARNING;
        } else if ("INFORMATION".equalsIgnoreCase(verbosity)) {
            this.verbosity = INFORMATION;
        } else if ("DEBUG".equalsIgnoreCase(verbosity)) {
            this.verbosity = DEBUG;
        }
    }

    @Override
    public abstract void log(String msg);


    @Override
    public void log(Exception e, String msg) {
        log(msg, e);
    }

    @Override
    public void log(String msg, Throwable throwable) {
        CharArrayWriter buf = new CharArrayWriter();
        PrintWriter writer = new PrintWriter(buf);
        writer.println(msg);
        throwable.printStackTrace(writer);
        Throwable rootCause = null;
        if (throwable instanceof ServletException) {
            rootCause = ((ServletException) throwable).getRootCause();
        }
        if (rootCause != null) {
            writer.println("---- Root Cause ----");
            rootCause.printStackTrace(writer);
        }
        log(buf.toString());
    }

    @Override
    public void log(String msg, int verbosity) {
        if (this.verbosity >= verbosity) {
            log(msg);
        }
    }

    @Override
    public void log(String msg, Throwable throwable, int verbosity) {
        if (this.verbosity >= verbosity) {
            log(msg, throwable);
        }
    }
}
