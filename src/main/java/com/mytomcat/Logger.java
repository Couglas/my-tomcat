package com.mytomcat;

/**
 * 日志
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public interface Logger {
    int FATAL = Integer.MIN_VALUE;
    int ERROR = 1;
    int WARNING = 2;
    int INFORMATION = 3;
    int DEBUG = 4;

    String getInfo();

    int getVerbosity();

    void setVerbosity(int verbosity);

    void log(String msg);

    void log(Exception e, String msg);

    void log(String msg, Throwable throwable);

    void log(String msg, int verbosity);

    void log(String msg, Throwable throwable, int verbosity);
}
