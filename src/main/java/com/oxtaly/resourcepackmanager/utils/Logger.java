package com.oxtaly.resourcepackmanager.utils;

import org.jetbrains.annotations.NotNull;

public class Logger {
    private org.slf4j.Logger logger;
    private String tag;

    public org.slf4j.Logger getLogger() {
        return logger;
    }

    public Logger(@NotNull org.slf4j.Logger logger, String tag) {
        this.logger = logger;
        this.tag = tag;
    }

    public void info(String msg) {
        this.logger.info(this.tag + msg);
    }

    public void debug(String msg) {
        this.logger.debug(this.tag + msg);
    }

    public void warn(String msg) {
        this.logger.warn(this.tag + msg);
    }

    public void error(String msg) {
        this.logger.error(this.tag + msg);
    }
    public void error(String msg, Throwable t) {
        this.logger.error(this.tag + msg, t);
    }
}
