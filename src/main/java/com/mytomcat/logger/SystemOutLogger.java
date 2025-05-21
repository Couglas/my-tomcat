package com.mytomcat.logger;

/**
 * 系统输出日志
 *
 * @author zhenxingchen4
 * @since 2025/5/20
 */
public class SystemOutLogger extends LoggerBase {
    protected static final String info = "System Out Logger, version 0.1";

    @Override
    public void log(String msg) {
        System.out.println(msg);
    }
}
