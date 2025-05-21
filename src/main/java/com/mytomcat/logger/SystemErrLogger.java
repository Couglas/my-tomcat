package com.mytomcat.logger;

/**
 * 系统错误日志
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public class SystemErrLogger extends LoggerBase {
    protected static final String info = "System Error Logger, version 0.1";

    @Override
    public void log(String msg) {
        System.err.println(msg);
    }

    @Override
    public String getInfo() {
        return info;
    }
}
